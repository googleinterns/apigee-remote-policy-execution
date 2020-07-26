/*
 * Copyright 2020 Google LLC
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>https://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.apigee;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.execution.spi.Execution;
import com.apigee.flow.message.MessageContext;
import com.google.apigee.ProtoMessageBuilders.ExecutionContextProtoMessageBuilder;
import com.google.apigee.ProtoMessageBuilders.ExecutionProtoMessageBuilder;
import com.google.apigee.ProtoMessageBuilders.MessageContextProtoMessageBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Java Callout demonstrating remote execution of a Java Callout or Apigee Policy on Google Cloud
 * Functions.
 */
public class JavaCalloutRPE implements Execution {

  // URL of the Cloud Functions endpoint
  private final String CLOUD_FUNCTIONS_URL = "";

  /**
   * Constructs a Protocol Buffer Message using the {@link MessageContext} and {@link
   * ExecutionContext} objects and sends them over to Cloud Functions for execution.
   *
   * @param messageContext Object allowing access to entities inside the flow
   * @param executionContext Object allowing access to proxy execution context
   * @return A successful execution after response is received from Cloud Functions
   */
  public ExecutionResult execute(MessageContext messageContext, ExecutionContext executionContext) {
    try {
      ExecutionProtoMessageBuilder executionProtoMessageBuilder =
          new ExecutionProtoMessageBuilder(
              new ExecutionContextProtoMessageBuilder(), new MessageContextProtoMessageBuilder());
      ExecutionOuterClass.Execution executionMessage =
          executionProtoMessageBuilder.buildExecutionMessage(messageContext, executionContext);
      RemotePolicyExecutionHandler remotePolicyExecutionHandler =
          new RemotePolicyExecutionHandler();
      messageContext
          .getMessage()
          .setContent(
              remotePolicyExecutionHandler.sendCloudFunctionsRequest(
                  executionMessage, CLOUD_FUNCTIONS_URL));
      return ExecutionResult.SUCCESS;
    } catch (Exception exception) {
      messageContext.setVariable("MORGAN1", exception.toString());
      String stackTrace = getStackTrace(exception);
      messageContext.setVariable("MORGAN2", stackTrace);
      return ExecutionResult.ABORT;
    }
  }

  private String getStackTrace(final Throwable throwable) {
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw, true);
    throwable.printStackTrace(pw);
    return sw.getBuffer().toString();
  }
}

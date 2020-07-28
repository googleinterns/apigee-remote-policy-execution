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

/**
 * Java Callout demonstrating execution of a Java Callout or Apigee Policy on remote HTTP Server.
 */
public class JavaCalloutRemotePolicyExecution implements Execution {

  // URL of the HTTP Server endpoint
  private final String URL_STRING = "";

  /**
   * Constructs a Protocol Buffer Message using the {@link MessageContext} and {@link
   * ExecutionContext} objects and sends them over to Cloud Functions for execution.
   *
   * @param messageContext Object allowing access to entities inside the flow
   * @param executionContext Object allowing access to proxy execution context
   * @return A successful execution after response is received from HTTP Server
   */
  public ExecutionResult execute(MessageContext messageContext, ExecutionContext executionContext) {
    try {
      ExecutionProtoMessageBuilder executionProtoMessageBuilder =
          new ExecutionProtoMessageBuilder(
              new ExecutionContextProtoMessageBuilder(), new MessageContextProtoMessageBuilder());
      Execute.Execution executionMessage =
          executionProtoMessageBuilder.buildExecutionMessage(messageContext, executionContext);
      RemotePolicyExecutionHandler remotePolicyExecutionHandler =
          new RemotePolicyExecutionHandler();
      String remoteExecutionResult =
          remotePolicyExecutionHandler.sendCloudFunctionsRequest(executionMessage, URL_STRING);
      messageContext.getMessage().setContent(remoteExecutionResult);
      return ExecutionResult.SUCCESS;
    } catch (Throwable throwable) {
      return ExecutionResult.ABORT;
    }
  }
}

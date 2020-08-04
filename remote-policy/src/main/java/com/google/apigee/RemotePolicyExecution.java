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

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Decodes an Execution Protocol Buffer Message and sets a flow variable before returning to caller.
 * Designed to be deployed as a Google Cloud Function.
 */
public class RemotePolicyExecution implements HttpFunction {

  /**
   * Reads and decodes the Execution Protocol Buffer Message and sets a flow variable and returns
   * the Execution Message back to caller.
   *
   * @param request contents of HTTP request containing the Protocol Buffer Message bytes
   * @param response HttpResponse sent in response to Http request containing modified Protocol
   *     Buffer Message with new flow variable set
   * @throws IOException
   */
  @Override
  public void service(HttpRequest request, HttpResponse response) throws IOException {
    try {
      InputStream is = request.getInputStream();
      byte[] data = is.readAllBytes();
      Execute.Execution execution = Execute.Execution.parseFrom(data);
      Execute.Execution.Builder executionBuilder = execution.toBuilder();
      executionBuilder
          .getMessageContextBuilder()
          .getTargetRequestMessageBuilder()
          .putFlowVariables(
              "Example",
              Execute.Message.FlowMapValue.newBuilder().setFlowVariable("Hello").build());

      executionBuilder.setExecutionResult(
          Execute.ExecutionResult.newBuilder().setAction(Execute.ExecutionResult.Action.CONTINUE));

      execution = executionBuilder.build();
      response.getOutputStream().write(execution.toByteArray());
    } catch (Throwable throwable) {
      Execute.Execution execution =
          Execute.Execution.newBuilder()
              .setExecutionResult(
                  Execute.ExecutionResult.newBuilder()
                      .setAction(Execute.ExecutionResult.Action.ABORT)
                      .setErrorResponse(throwable.toString()))
              .build();
      response.getOutputStream().write(execution.toByteArray());
    }
  }
}

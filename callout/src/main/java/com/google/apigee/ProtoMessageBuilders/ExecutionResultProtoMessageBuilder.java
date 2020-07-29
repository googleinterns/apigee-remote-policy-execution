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

package com.google.apigee.ProtoMessageBuilders;

import com.apigee.flow.execution.Action;
import com.apigee.flow.execution.ExecutionResult;
import com.google.apigee.Execute;

import java.util.stream.Collectors;

/**
 * Builder class that constructs an ExecutionResult Protocol Buffer Message from a {@link
 * ExecutionResult} object
 */
public class ExecutionResultProtoMessageBuilder {
  /**
   * Builds an ExecutionResult Protocol Buffer Message using a {@link ExecutionResult} object.
   *
   * @param executionResult {@link ExecutionResult} object to build the protocol buffer message
   *     from.
   * @return ExecutionResult Protocol Buffer Message
   */
  public static Execute.ExecutionResult buildExecutionResultMessage(
      ExecutionResult executionResult) {
    Execute.ExecutionResult.Builder executionResultBuilder =
        Execute.ExecutionResult.newBuilder().setAction(getExecutionResultAction(executionResult));

    if (executionResult.getErrorResponseHeaders() != null) {
      executionResultBuilder.putAllErrorResponseHeaders(executionResult.getErrorResponseHeaders());
    }
    if (executionResult.getProperties() != null) {
      executionResultBuilder.putAllProperties(
          executionResult.getProperties().entrySet().stream()
              .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())));
    }
    if (executionResult.getErrorResponse() != null) {
      executionResultBuilder.setErrorResponse(executionResult.getErrorResponse());
    }
    return executionResultBuilder.build();
  }

  /**
   * Determines mapping of {@link Action} to Execute.ExecutionResult.Action enumerator value.
   *
   * @param executionResult {@link ExecutionResult} object to extract Action from.
   * @return Execute.ExecutionResult.Action enumerator value
   */
  private static Execute.ExecutionResult.Action getExecutionResultAction(
      ExecutionResult executionResult) {
    switch (executionResult.getAction()) {
      case CONTINUE:
        return Execute.ExecutionResult.Action.CONTINUE;
      case PAUSE:
        return Execute.ExecutionResult.Action.PAUSE;
      case ABORT:
        return Execute.ExecutionResult.Action.ABORT;
      default:
        return Execute.ExecutionResult.Action.UNKNOWN_ACTION;
    }
  }
}

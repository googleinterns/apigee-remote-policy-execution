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

import com.apigee.flow.Fault;
import com.apigee.flow.execution.ExecutionContext;
import com.google.apigee.Execute;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builder class that constructs an ExecutionContext Protocol Buffer Message from a {@link
 * ExecutionContext} object
 */
public class ExecutionContextProtoMessageBuilder {
  /**
   * Builds an ExecutionContext Protocol Buffer Message from a {@link ExecutionContext} object.
   *
   * @param executionContext {@link ExecutionContext} object from which to build the Protocol Buffer
   *     Message.
   * @return ExecutionContext Protocol Buffer Message
   */
  public static Execute.ExecutionContext buildExecutionContextProto(
      ExecutionContext executionContext) {
    Execute.ExecutionContext.Builder executionContextBuilder =
        Execute.ExecutionContext.newBuilder();
    executionContextBuilder.setFlowType(getFlowType(executionContext));
    if (executionContext.getFaults() != null) {
      executionContextBuilder.addAllFaults(
          executionContext.getFaults().stream()
              .map(ExecutionContextProtoMessageBuilder::buildFault)
              .collect(Collectors.toList()));
    }
    return executionContextBuilder.build();
  }

  /**
   * Gets the appropriate FlowType enumerator from the {@link ExecutionContext}. Used to indicate an
   * error or request flow.
   *
   * @param executionContext {@link ExecutionContext} from which to extract the flow type.
   * @return ExecutionContext.FlowType enumerator value
   */
  private static Execute.ExecutionContext.FlowType getFlowType(ExecutionContext executionContext) {
    if (executionContext.isErrorFlow()) {
      return Execute.ExecutionContext.FlowType.ERROR;
    } else if (executionContext.isRequestFlow()) {
      return Execute.ExecutionContext.FlowType.REQUEST;
    } else {
      return Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE;
    }
  }

  /**
   * Builds a Fault Protocol Buffer Message using a {@link Fault} object.
   *
   * @param fault {@link Fault} used to construct the Protocol Buffer Message.
   * @return Fault Protocol Buffer Message
   */
  private static Execute.ExecutionContext.Fault buildFault(Fault fault) {
    Execute.ExecutionContext.Fault.Builder faultBuilder =
        Execute.ExecutionContext.Fault.newBuilder()
            .setCategory(getFaultCategory(fault))
            .setSubCategory(fault.getSubCategory())
            .setName(fault.getName());
    if (fault.getAttributes() != null) {
      faultBuilder.putAllAttributes(
          fault.getAttributes().entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue())));
    }
    if (fault.getReason() != null) {
      faultBuilder.setReason(fault.getReason());
    }
    return faultBuilder.build();
  }

  /**
   * Determines the appropriate Category for the Fault Protocol Buffer Message
   *
   * @param fault Fault from which to extract the Category
   * @return Fault.Category Protocol Buffer enumerator value
   */
  private static Execute.ExecutionContext.Fault.Category getFaultCategory(Fault fault) {
    switch (fault.getCategory()) {
      case Messaging:
        return Execute.ExecutionContext.Fault.Category.MESSAGING;
      case Step:
        return Execute.ExecutionContext.Fault.Category.STEP;
      case Transport:
        return Execute.ExecutionContext.Fault.Category.TRANSPORT;
      case System:
        return Execute.ExecutionContext.Fault.Category.SYSTEM;
      default:
        return Execute.ExecutionContext.Fault.Category.UNKNOWN_CATEGORY;
    }
  }
}

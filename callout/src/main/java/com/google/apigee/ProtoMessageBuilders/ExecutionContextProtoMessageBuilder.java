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
import com.google.apigee.ExecutionOuterClass;
import com.google.protobuf.Any;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
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
  public ExecutionOuterClass.ExecutionContext buildExecutionContextProto(
      ExecutionContext executionContext) {
    return ExecutionOuterClass.ExecutionContext.newBuilder()
        .setFlowType(getFlowType(executionContext))
        .addAllFaults(
            executionContext.getFaults().stream()
                .map(this::buildFault)
                .collect(Collectors.toList()))
        .build();
  }

  /**
   * Gets the appropriate FlowType enumerator from the {@link ExecutionContext}. Used to indicate an
   * error or request flow.
   *
   * @param executionContext {@link ExecutionContext} from which to extract the flow type.
   * @return ExecutionContext.FlowType enumerator value
   */
  private ExecutionOuterClass.ExecutionContext.FlowType getFlowType(
      ExecutionContext executionContext) {
    if (executionContext.isErrorFlow()) {
      return ExecutionOuterClass.ExecutionContext.FlowType.ERROR;
    }
    if (executionContext.isRequestFlow()) {
      return ExecutionOuterClass.ExecutionContext.FlowType.REQUEST;
    }
    return ExecutionOuterClass.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE;
  }

  /**
   * Builds a Fault Protocol Buffer Message using a {@link Fault} object.
   *
   * @param fault {@link Fault} used to construct the Protocol Buffer Message.
   * @return Fault Protocol Buffer Message
   */
  private ExecutionOuterClass.ExecutionContext.Fault buildFault(Fault fault) {
    return ExecutionOuterClass.ExecutionContext.Fault.newBuilder()
        .setCategory(getFaultCategory(fault))
        .setSubCategory(fault.getSubCategory())
        .setName(fault.getName())
        .setReason(fault.getReason())
        .putAllAttributes(buildAttributesMap(fault))
        .build();
  }

  /**
   * Determines the appropriate Category for the Fault Protocol Buffer Message
   *
   * @param fault Fault from which to extract the Category
   * @return Fault.Category Protocol Buffer enumerator value
   */
  private ExecutionOuterClass.ExecutionContext.Fault.Category getFaultCategory(Fault fault) {
    switch (fault.getCategory()) {
      case Messaging:
        return ExecutionOuterClass.ExecutionContext.Fault.Category.MESSAGING;
      case Step:
        return ExecutionOuterClass.ExecutionContext.Fault.Category.STEP;
      case Transport:
        return ExecutionOuterClass.ExecutionContext.Fault.Category.TRANSPORT;
      case System:
        return ExecutionOuterClass.ExecutionContext.Fault.Category.SYSTEM;
      default:
        return ExecutionOuterClass.ExecutionContext.Fault.Category.UNKNOWN_CATEGORY;
    }
  }

  /**
   * Constructs the String to Any attributes map for a Fault protocol buffer message
   *
   * @param fault Fault from which attributes map is extracted from
   * @return Map of String to Any for protocol buffer message
   */
  private Map<String, Any> buildAttributesMap(Fault fault) {
    Map<String, Any> attributesMap = new HashMap<>();
    fault
        .getAttributes()
        .forEach(
            (key, value) -> {
              try {
                attributesMap.put(key, Any.parseFrom(objectToByteArray(value)));
              } catch (Exception exception) {
                exception.printStackTrace();
              }
            });
    return attributesMap;
  }

  /**
   * Converts an object to a byte array. Used to convert the values in attributes map of a Fault.
   *
   * @param obj Object to convert to byte array
   * @return A byte array of the object
   * @throws Exception IOExceptions from ObjectOutputStream
   */
  private byte[] objectToByteArray(Object obj) throws Exception {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    objectOutputStream.writeObject(obj);
    return byteArrayOutputStream.toByteArray();
  }
}

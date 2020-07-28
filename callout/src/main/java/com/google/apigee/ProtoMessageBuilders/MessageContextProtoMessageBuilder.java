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

import com.apigee.flow.FlowInfo;
import com.apigee.flow.message.FlowContext;
import com.apigee.flow.message.Message;
import com.apigee.flow.message.MessageContext;
import com.google.apigee.Execute;
import com.google.protobuf.ByteString;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder class that constructs an MessageContext Protocol Buffer Message from a {@link
 * MessageContext} object.
 */
public class MessageContextProtoMessageBuilder {
  /**
   * Builds a MessageContext Protocol Buffer Message using a {@link MessageContext} object.
   *
   * @param messageContext {@link MessageContext} object used to construct the MessageContext
   *     Protocol Buffer Message.
   * @return MessageContext Protocol Buffer Message
   */
  public Execute.MessageContext buildMessageContextProto(MessageContext messageContext) {
    Execute.MessageContext.Builder messageContextBuilder = Execute.MessageContext.newBuilder();
    if (messageContext.getMessage(FlowContext.TARGET_REQUEST) != null) {
      messageContextBuilder.setTargetRequestMessage(
          buildMessageProto(messageContext.getMessage(FlowContext.TARGET_REQUEST)));
    }
    if (messageContext.getMessage(FlowContext.PROXY_REQUEST) != null) {
      messageContextBuilder.setProxyRequestMessage(
          buildMessageProto(messageContext.getMessage(FlowContext.PROXY_REQUEST)));
    }
    if (messageContext.getMessage(FlowContext.TARGET_RESPONSE) != null) {
      messageContextBuilder.setTargetResponseMessage(
          buildMessageProto(messageContext.getMessage(FlowContext.TARGET_RESPONSE)));
    }
    if (messageContext.getMessage(FlowContext.PROXY_RESPONSE) != null) {
      messageContextBuilder.setProxyResponseMessage(
          buildMessageProto(messageContext.getMessage(FlowContext.PROXY_RESPONSE)));
    }
    if (messageContext.getErrorMessage() != null) {
      messageContextBuilder.setErrorMessage(buildMessageProto(messageContext.getErrorMessage()));
    }
    return messageContextBuilder.build();
  }

  /**
   * Builds a Message Protocol Buffer Message using a {@link Message} object.
   *
   * @param message {@link Message} object used to construct the Message Protocol Buffer Message.
   * @return Message Protocol Buffer Message
   */
  private Execute.Message buildMessageProto(Message message) {
    Execute.Message.Builder messageBuilder = Execute.Message.newBuilder();
    if (message.getContent() != null) {
      messageBuilder.setContent(ByteString.copyFrom(message.getContent(), StandardCharsets.UTF_8));
    }
    if (message.getHeaderNames() != null) {
      messageBuilder.putAllHeaderMap(buildHeaderMap(message));
    }
    if (message.getQueryParamNames() != null) {
      messageBuilder.putAllQueryParamMap(buildQueryParametersMap(message));
    }
    messageBuilder.putAllFlowVariables(new HashMap<>());
    return messageBuilder.build();
  }

  /**
   * Builds the FlowMapValue for the Flow Variable map in the Message Protocol Buffer Message.
   *
   * @param value Either a String or FlowInfo object to store in the Flow Variable map.
   * @return FlowMapValue to be stored in the Flow Variable map in the Message Protocol Buffer
   *     Message.
   */
  private Execute.Message.FlowMapValue buildFlowMapValue(Object value) {
    if (!(value instanceof FlowInfo) && !(value instanceof String)) {
      throw new IllegalArgumentException();
    }
    if (value instanceof FlowInfo) {
      return Execute.Message.FlowMapValue.newBuilder()
          .setFlowInfo((buildFlowInfoProto((FlowInfo) value)))
          .build();
    } else {
      return Execute.Message.FlowMapValue.newBuilder().setFlowVariable((String) value).build();
    }
  }

  /**
   * Builds a FlowInfo Protocol Buffer Message from a {@link FlowInfo} object.
   *
   * @param flowInfo {@link FlowInfo} object used to construct the FlowInfo Protocol Buffer Message.
   * @return FlowInfo Protocol Buffer Message
   */
  private Execute.FlowInfo buildFlowInfoProto(FlowInfo flowInfo) {
    return Execute.FlowInfo.newBuilder()
        .setIdentifier(flowInfo.getIdentifier())
        .putAllVariables(new HashMap<>())
        .build();
  }

  /**
   * Builds the Header Map in the Message Protocol Buffer Message using the same field in {@link
   * Message}.
   *
   * @param message {@link Message} object from which to extract the header map
   * @return Map of String to Headers Protocol Buffer Message which is just a list of String
   */
  private Map<String, Execute.Message.Headers> buildHeaderMap(Message message) {
    Map<String, Execute.Message.Headers> headerMap = new HashMap<>();
    message
        .getHeaderNames()
        .forEach(
            headerName ->
                headerMap.put(
                    headerName,
                    Execute.Message.Headers.newBuilder()
                        .addAllHeaders(message.getHeaders(headerName))
                        .build()));
    return headerMap;
  }

  /**
   * Builds the Query Parameter Map in the Message Protocol Buffer Message using the same field in
   * {@link Message}.
   *
   * @param message {@link Message} object from which to extract the query parameters map.
   * @return Map of String to QueryParameters Protocol Buffer Message which is just a list of String
   */
  private Map<String, Execute.Message.QueryParameters> buildQueryParametersMap(Message message) {
    Map<String, Execute.Message.QueryParameters> queryParametersMap = new HashMap<>();
    message
        .getQueryParamNames()
        .forEach(
            queryParamName ->
                queryParametersMap.put(
                    queryParamName,
                    Execute.Message.QueryParameters.newBuilder()
                        .addAllQueryParameters(message.getHeaders(queryParamName))
                        .build()));
    return queryParametersMap;
  }
}

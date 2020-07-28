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

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.message.MessageContext;
import com.google.apigee.Execute;

/**
 * Builder for Execution Protocol Buffer Message which handles the construction of the
 * ExecutionContext and MessageContext Protocol Buffer Messages.
 */
public class ExecutionProtoMessageBuilder {

  private final ExecutionContextProtoMessageBuilder executionContextProtoMessageBuilder;
  private final MessageContextProtoMessageBuilder messageContextProtoMessageBuilder;

  /**
   * @param executionContextProtoMessageBuilder Builder for ExecutionContext Protocol Buffer
   *     Messages.
   * @param messageContextProtoMessageBuilder Builder for MessageContext Protocol Buffer Messages.
   */
  public ExecutionProtoMessageBuilder(
      ExecutionContextProtoMessageBuilder executionContextProtoMessageBuilder,
      MessageContextProtoMessageBuilder messageContextProtoMessageBuilder) {
    this.executionContextProtoMessageBuilder = executionContextProtoMessageBuilder;
    this.messageContextProtoMessageBuilder = messageContextProtoMessageBuilder;
  }

  /**
   * Builds an Execution Protocol Buffer Message which contains the ExecutionContext and
   * MessageContext Protocol Buffer Messages.
   *
   * @param messageContext {@link MessageContext} object to construct the MessageContext Protocol
   *     Buffer Message.
   * @param executionContext {@link ExecutionContext} object to construct the ExecutionContext
   *     Protocol Buffer Message.
   * @return Execution Protocol Buffer Message containing the ExecutionContext and MessageContext
   *     Protocol Buffer Messages
   */
  public Execute.Execution buildExecutionMessage(
      MessageContext messageContext, ExecutionContext executionContext) throws Exception {
    Execute.ExecutionContext executionContextProtoMessage =
        executionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);
    Execute.MessageContext messageContextProtoMessage =
        messageContextProtoMessageBuilder.buildMessageContextProto(messageContext);
    return Execute.Execution.newBuilder()
        .setExecutionContext(executionContextProtoMessage)
        .setMessageContext(messageContextProtoMessage)
        .build();
  }
}

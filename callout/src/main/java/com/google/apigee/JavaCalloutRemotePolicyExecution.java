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
import com.google.apigee.Execute.ExecutionResult.Action;
import com.google.apigee.ProtoMessageBuilders.ExecutionContextProtoMessageBuilder;
import com.google.apigee.ProtoMessageBuilders.MessageContextProtoMessageBuilder;
import java.util.Map;
import java.util.Properties;

/**
 * Java Callout demonstrating execution of a Java Callout or Apigee Policy on remote HTTP Server.
 */
public class JavaCalloutRemotePolicyExecution implements Execution {

  private final String FLOW_VARIABLE_KEY = "Example";
  private final String REMOTE_SERVER_URL_PROPERTY_NAME = "remote_execution_url";
  private final RemotePolicyExecutionHandler remotePolicyExecutionHandler;
  private Map<String, String> properties;

  public JavaCalloutRemotePolicyExecution(Map<String, String> properties) {
    this(properties, new RemotePolicyExecutionHandler());
  }

  public JavaCalloutRemotePolicyExecution(
      Map<String, String> properties, RemotePolicyExecutionHandler remotePolicyExecutionHandler) {
    this.properties = properties;
    this.remotePolicyExecutionHandler = remotePolicyExecutionHandler;
  }

  /**
   * Constructs a Protocol Buffer Message using the {@link MessageContext} and {@link
   * ExecutionContext} objects and sends them over to the remote HTTP server for execution. HTTP
   * Server URL is retrieved from a flow variable.
   *
   * @param messageContext Object allowing access to entities inside the flow
   * @param executionContext Object allowing access to proxy execution context
   * @return A successful execution after response is received from HTTP Server
   */
  public ExecutionResult execute(MessageContext messageContext, ExecutionContext executionContext) {
    try {
      // URL of the HTTP Server endpoint set in a flow variable.
      String serverUrl = this.properties.get(REMOTE_SERVER_URL_PROPERTY_NAME);
      Execute.Execution execution =
          Execute.Execution.newBuilder()
              .setExecutionContext(
                  ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext))
              .setMessageContext(
                  MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext))
              .build();
      Execute.Execution remoteExecutionResult =
          remotePolicyExecutionHandler.sendRemoteHttpServerRequest(execution, serverUrl);
      ExecutionResult executionResult =
          remoteExecutionResult.hasExecutionResult()
              ? extractExecutionResult(remoteExecutionResult.getExecutionResult())
              : ExecutionResult.ABORT;
      if (executionResult.getAction() == com.apigee.flow.execution.Action.CONTINUE) {
        messageContext
            .getMessage()
            .setContent(extractFlowVariable(remoteExecutionResult, FLOW_VARIABLE_KEY));
      }
      return executionResult;
    } catch (Throwable throwable) {
      messageContext.setVariable("callout_exception", throwable);
      return ExecutionResult.ABORT;
    }
  }

  /**
   * Builds an {@link ExecutionResult} object using the ExecutionResult Protocol Buffer Message in
   * response from remote HTTP Server.
   *
   * @param executionResultMessage ExecutionResult Protocol Buffer Message from remote HTTP Server.
   * @return {@link ExecutionResult} object representing result of execution
   */
  private ExecutionResult extractExecutionResult(Execute.ExecutionResult executionResultMessage) {
    ExecutionResult executionResult;
    if (executionResultMessage.getAction() == Action.ABORT) {
      executionResult = new ExecutionResult(false, com.apigee.flow.execution.Action.ABORT);
    } else if (executionResultMessage.getAction() == Action.PAUSE) {
      executionResult = new ExecutionResult(true, com.apigee.flow.execution.Action.PAUSE);
    } else {
      executionResult = new ExecutionResult(true, com.apigee.flow.execution.Action.CONTINUE);
    }
    executionResult.setErrorResponse(executionResultMessage.getErrorResponse());
    Properties properties = new Properties();
    properties.putAll(executionResultMessage.getPropertiesMap());
    executionResult.setProperties(properties);
    executionResult.setErrorResponseHeaders(executionResultMessage.getErrorResponseHeadersMap());
    return executionResult;
  }

  /**
   * Extracts the flow variable set by the remote HTTP server.
   *
   * @param execution Execute.Execution Protocol Buffer Message.
   * @param key String of the flow variable key to get
   * @return String value of flow variable value
   */
  private String extractFlowVariable(Execute.Execution execution, String key) {
    return execution
        .getMessageContext()
        .getTargetRequestMessage()
        .getFlowVariablesMap()
        .get(key)
        .getFlowVariable();
  }
}

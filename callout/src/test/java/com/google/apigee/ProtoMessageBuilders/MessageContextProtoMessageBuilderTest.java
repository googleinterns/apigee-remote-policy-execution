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

import static org.junit.Assert.assertEquals;

import com.apigee.flow.FlowInfo;
import com.apigee.flow.message.FlowContext;
import com.apigee.flow.message.Message;
import com.apigee.flow.message.MessageContext;
import com.google.apigee.Execute;
import com.google.protobuf.TextFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class MessageContextProtoMessageBuilderTest {

  private final String CONTENT = "content1";
  private final String FLOW_INFO_IDENTIFIER = "identifier";
  private final String KEY1 = "key1";
  private final String KEY2 = "key2";
  private final String VAL1 = "val1";
  private final String VAL2 = "val2";

  @Spy private FakeMessageContext messageContext;
  @Spy private FakeMessage message;
  @Spy private FakeFlowInfo flowInfo;

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);

    messageContext.clearAll();
    message.clearAll();
  }

  @Test
  public void testBuildMessageContextWithTargetRequestMessage() throws Exception {
    Execute.MessageContext.Builder expectedProtoBuilder = Execute.MessageContext.newBuilder();
    TextFormat.merge(
        "target_request_message {}",
        expectedProtoBuilder);

    messageContext.setMessage(FlowContext.TARGET_REQUEST, message);
    Execute.MessageContext actualProto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildMessageContextWithTargetResponseMessage() throws Exception {
    Execute.MessageContext.Builder expectedProtoBuilder = Execute.MessageContext.newBuilder();
    TextFormat.merge(
        "target_response_message {}",
        expectedProtoBuilder);
    messageContext.setMessage(FlowContext.TARGET_RESPONSE, message);
    Execute.MessageContext actualProto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildMessageContextWithProxyRequestMessage() throws Exception {
    Execute.MessageContext.Builder expectedProtoBuilder = Execute.MessageContext.newBuilder();
    TextFormat.merge(
        "proxy_request_message {}",
        expectedProtoBuilder);
    messageContext.setMessage(FlowContext.PROXY_REQUEST, message);
    Execute.MessageContext actualProto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildMessageContextWithProxyResponseMessage() throws Exception {
    Execute.MessageContext.Builder expectedProtoBuilder = Execute.MessageContext.newBuilder();
    TextFormat.merge(
        "proxy_response_message {}",
        expectedProtoBuilder);
    messageContext.setMessage(FlowContext.PROXY_RESPONSE, message);
    Execute.MessageContext actualProto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildMessageContextWithErrorMessage() throws Exception {
    Execute.MessageContext.Builder expectedProtoBuilder = Execute.MessageContext.newBuilder();
    TextFormat.merge(
        "error_message {}",
        expectedProtoBuilder);
    messageContext.setErrorMessage(message);
    Execute.MessageContext actualProto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildMessageWithHeaderMap() throws Exception {
    Execute.MessageContext.Builder expectedProtoBuilder = Execute.MessageContext.newBuilder();
    TextFormat.merge(
        "target_request_message {"
            + "    header_map {"
            + "        key: \"" + KEY1 + "\""
            + "        value {"
            + "            headers: [\"" + VAL1 + "\", \"" + VAL2 + "\"]"
            + "        }"
            + "    }"
            + "    header_map {"
            + "        key: \"" + KEY2 + "\""
            + "        value {"
            + "            headers: [\"" + VAL2 + "\"]"
            + "        }"
            + "    }"
            + "}",
        expectedProtoBuilder);

    message.setHeader(KEY1, VAL1);
    message.setHeader(KEY1, VAL2);
    message.setHeader(KEY2, VAL2);
    messageContext.setMessage(FlowContext.TARGET_REQUEST, message);
    Execute.MessageContext actualProto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildMessageWithContent() throws Exception {
    Execute.MessageContext.Builder expectedProtoBuilder = Execute.MessageContext.newBuilder();
    TextFormat.merge(
        "target_request_message {" + "    content: \"" + CONTENT + "\"" + "}",
        expectedProtoBuilder);
    message.setContent(CONTENT);
    messageContext.setMessage(FlowContext.TARGET_REQUEST, message);
    Execute.MessageContext actualProto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildMessageWithQueryParamMap() throws Exception {
    Execute.MessageContext.Builder expectedProtoBuilder = Execute.MessageContext.newBuilder();
    TextFormat.merge(
        "target_request_message {"
            + "    query_param_map {"
            + "        key: \"" + KEY1 + "\""
            + "        value {"
            + "            query_parameters: [\"" + VAL1 + "\", \"" + VAL2 + "\"]"
            + "        }"
            + "    }"
            + "    query_param_map {"
            + "        key: \"" + KEY2 + "\""
            + "        value {"
            + "            query_parameters: [\"" + VAL2 + "\"]"
            + "        }"
            + "    }"
            + "}",
        expectedProtoBuilder);

    message.setQueryParam(KEY1, VAL1);
    message.setQueryParam(KEY1, VAL2);
    message.setQueryParam(KEY2, VAL2);
    messageContext.setMessage(FlowContext.TARGET_REQUEST, message);
    Execute.MessageContext actualProto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildFlowMapValueString() throws Exception {
    Execute.Message.FlowMapValue.Builder expectedProtoBuilder =
        Execute.Message.FlowMapValue.newBuilder();
    TextFormat.merge(
        "flow_variable: \"" + VAL1 + "\"",
        expectedProtoBuilder);

    Execute.Message.FlowMapValue actualProto =
        MessageContextProtoMessageBuilder.buildFlowMapValue(VAL1);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildFlowMapValueFlowInfo() throws Exception {
    flowInfo.setIdentifier(FLOW_INFO_IDENTIFIER);
    Execute.Message.FlowMapValue.Builder expectedProtoBuilder =
        Execute.Message.FlowMapValue.newBuilder();
    TextFormat.merge(
        "flow_info {" + "    identifier: \"" + FLOW_INFO_IDENTIFIER + "\"" + "}",
        expectedProtoBuilder);

    Execute.Message.FlowMapValue actualProto =
        MessageContextProtoMessageBuilder.buildFlowMapValue(flowInfo);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBuildFlowMapValueIllegalArgument() {
    MessageContextProtoMessageBuilder.buildFlowMapValue(0);
  }

  abstract static class FakeMessageContext implements MessageContext {
    private Message proxyRequestMessage;
    private Message targetRequestMessage;
    private Message proxyResponseMessage;
    private Message targetResponseMessage;
    private Message errorMessage;

    @Override
    public Message getMessage(FlowContext flowContext) {
      switch (flowContext) {
        case PROXY_REQUEST:
          return this.proxyRequestMessage;
        case TARGET_REQUEST:
          return this.targetRequestMessage;
        case TARGET_RESPONSE:
          return this.targetResponseMessage;
        case PROXY_RESPONSE:
          return this.proxyResponseMessage;
        default:
          return null;
      }
    }

    @Override
    public void setMessage(FlowContext flowContext, Message message) {
      switch (flowContext) {
        case PROXY_REQUEST:
          this.proxyRequestMessage = message;
          break;
        case TARGET_REQUEST:
          this.targetRequestMessage = message;
          break;
        case TARGET_RESPONSE:
          this.targetResponseMessage = message;
          break;
        case PROXY_RESPONSE:
          this.proxyResponseMessage = message;
      }
    }

    @Override
    public Message getErrorMessage() {
      return this.errorMessage;
    }

    @Override
    public void setErrorMessage(Message message) {
      this.errorMessage = message;
    }

    public void clearAll() {
      proxyRequestMessage = null;
      proxyResponseMessage = null;
      targetRequestMessage = null;
      targetResponseMessage = null;
    }
  }

  abstract static class FakeMessage implements Message {

    private String content;
    private Map<String, List<String>> headers;
    private Map<String, List<String>> queryParams;
    private Map<String, Object> variables;

    @Override
    public Set<String> getHeaderNames() {
      if (headers == null) {
        return null;
      }
      return headers.keySet();
    }

    @Override
    public List<String> getHeaders(String headerName) {
      if (headers == null) {
        return null;
      }
      return headers.getOrDefault(headerName, null);
    }

    @Override
    public boolean setHeader(String headerName, Object headerValue) {
      if (headers == null) {
        headers = new HashMap<>();
      }
      headers.putIfAbsent(headerName, new ArrayList<>());
      headers.get(headerName).add((String) headerValue);
      return true;
    }

    @Override
    public String getContent() {
      return content;
    }

    @Override
    public void setContent(String content) {
      this.content = content;
    }

    @Override
    public Object getVariable(String key) {
      if (variables == null) {
        return null;
      }
      return variables.getOrDefault(key, null);
    }

    @Override
    public boolean setVariable(String key, Object value) {
      if (variables == null) {
        variables = new HashMap<>();
      }
      variables.put(key, value);
      return true;
    }

    @Override
    public Set<String> getQueryParamNames() {
      if (queryParams == null) {
        return null;
      }
      return queryParams.keySet();
    }

    @Override
    public List<String> getQueryParams(String queryParamName) {
      if (queryParams == null) {
        return null;
      }
      return queryParams.getOrDefault(queryParamName, null);
    }

    @Override
    public boolean setQueryParam(String queryParamName, Object queryParam) {
      if (queryParams == null) {
        queryParams = new HashMap<>();
      }
      queryParams.putIfAbsent(queryParamName, new ArrayList<>());
      queryParams.get(queryParamName).add((String) queryParam);
      return true;
    }

    public void clearAll() {
      if (queryParams != null) {
        queryParams.clear();
      }
      if (headers != null) {
        headers.clear();
      }
      if (variables != null) {
        variables.clear();
      }
      content = null;
    }
  }

  abstract static class FakeFlowInfo implements FlowInfo {
    private String identifier;

    @Override
    public String getIdentifier() {
      return identifier;
    }

    public void setIdentifier(String identifier) {
      this.identifier = identifier;
    }
  }
}

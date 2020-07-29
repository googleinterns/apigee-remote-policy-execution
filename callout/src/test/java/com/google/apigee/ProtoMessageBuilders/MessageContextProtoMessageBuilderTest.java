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

import com.apigee.flow.message.FlowContext;
import com.apigee.flow.message.Message;
import com.apigee.flow.message.MessageContext;
import com.google.apigee.Execute;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessageContextProtoMessageBuilderTest {

  private final String CONTENT = "content1";
  private final String KEY1 = "key1";
  private final String KEY2 = "key2";
  private final String VAL1 = "val1";
  private final String VAL2 = "val2";

  @Spy private FakeMessageContext messageContext;
  @Spy private FakeMessage message;

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);

    messageContext.clearAll();
    message.clearAll();
  }

  @Test
  public void testBuildMessageContextWithTargetRequestMessage() {
    messageContext.setMessage(FlowContext.TARGET_REQUEST, message);
    Execute.MessageContext proto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertTrue(proto.hasTargetRequestMessage());
    assertFalse(proto.hasTargetResponseMessage());
    assertFalse(proto.hasProxyRequestMessage());
    assertFalse(proto.hasProxyResponseMessage());
    assertFalse(proto.hasErrorMessage());
  }

  @Test
  public void testBuildMessageContextWithTargetResponseMessage() {
    messageContext.setMessage(FlowContext.TARGET_RESPONSE, message);
    Execute.MessageContext proto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertTrue(proto.hasTargetResponseMessage());
    assertFalse(proto.hasTargetRequestMessage());
    assertFalse(proto.hasProxyRequestMessage());
    assertFalse(proto.hasProxyResponseMessage());
    assertFalse(proto.hasErrorMessage());
  }

  @Test
  public void testBuildMessageContextWithProxyRequestMessage() {
    messageContext.setMessage(FlowContext.PROXY_REQUEST, message);
    Execute.MessageContext proto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertTrue(proto.hasProxyRequestMessage());
    assertFalse(proto.hasTargetResponseMessage());
    assertFalse(proto.hasTargetRequestMessage());
    assertFalse(proto.hasProxyResponseMessage());
    assertFalse(proto.hasErrorMessage());
  }

  @Test
  public void testBuildMessageContextWithProxyResponseMessage() {
    messageContext.setMessage(FlowContext.PROXY_RESPONSE, message);
    Execute.MessageContext proto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertTrue(proto.hasProxyResponseMessage());
    assertFalse(proto.hasTargetResponseMessage());
    assertFalse(proto.hasProxyRequestMessage());
    assertFalse(proto.hasTargetRequestMessage());
    assertFalse(proto.hasErrorMessage());
  }

  @Test
  public void testBuildMessageContextWithErrorMessage() {
    messageContext.setErrorMessage(message);
    Execute.MessageContext proto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertTrue(proto.hasErrorMessage());
    assertFalse(proto.hasTargetResponseMessage());
    assertFalse(proto.hasProxyRequestMessage());
    assertFalse(proto.hasTargetRequestMessage());
    assertFalse(proto.hasProxyResponseMessage());
  }

  @Test
  public void testBuildMessageWithHeaderMap() {
    message.setHeader(KEY1, VAL1);
    message.setHeader(KEY1, VAL2);
    message.setHeader(KEY2, VAL2);
    messageContext.setMessage(FlowContext.TARGET_REQUEST, message);
    Execute.MessageContext proto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertTrue(proto.hasTargetRequestMessage());
    assertEquals(2, proto.getTargetRequestMessage().getHeaderMapCount());
    assertEquals(buildHeadersMap(message), proto.getTargetRequestMessage().getHeaderMapMap());
  }

  private Map<String, Execute.Message.Headers> buildHeadersMap(Message message) {
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

  @Test
  public void testBuildMessageWithContent() {
    message.setContent(CONTENT);
    messageContext.setMessage(FlowContext.TARGET_REQUEST, message);
    Execute.MessageContext proto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertTrue(proto.hasTargetRequestMessage());
    assertEquals(CONTENT, proto.getTargetRequestMessage().getContent().toStringUtf8());
  }

  @Test
  public void testBuildMessageWithQueryParamMap() {
    message.setQueryParam(KEY1, VAL1);
    message.setQueryParam(KEY1, VAL2);
    message.setQueryParam(KEY2, VAL2);
    messageContext.setMessage(FlowContext.TARGET_REQUEST, message);
    Execute.MessageContext proto =
        MessageContextProtoMessageBuilder.buildMessageContextProto(messageContext);

    assertTrue(proto.hasTargetRequestMessage());
    assertEquals(2, proto.getTargetRequestMessage().getQueryParamMapCount());
    assertEquals(
        buildQueryParametersMap(message), proto.getTargetRequestMessage().getQueryParamMapMap());
  }

  private Map<String, Execute.Message.QueryParameters> buildQueryParametersMap(Message message) {
    Map<String, Execute.Message.QueryParameters> queryParametersMap = new HashMap<>();
    message
        .getQueryParamNames()
        .forEach(
            queryParamName ->
                queryParametersMap.put(
                    queryParamName,
                    Execute.Message.QueryParameters.newBuilder()
                        .addAllQueryParameters(message.getQueryParams(queryParamName))
                        .build()));
    return queryParametersMap;
  }

  abstract static class FakeMessageContext implements MessageContext {
    private Message proxyRequestMessage;
    private Message targetRequestMessage;
    private Message proxyResponseMessage;
    private Message targetResponseMessage;
    private Message errorMessage;

    @Override
    public Message getMessage(FlowContext var1) {
      switch (var1) {
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
    public void setMessage(FlowContext var1, Message var2) {
      switch (var1) {
        case PROXY_REQUEST:
          this.proxyRequestMessage = var2;
          break;
        case TARGET_REQUEST:
          this.targetRequestMessage = var2;
          break;
        case TARGET_RESPONSE:
          this.targetResponseMessage = var2;
          break;
        case PROXY_RESPONSE:
          this.proxyResponseMessage = var2;
      }
    }

    @Override
    public Message getErrorMessage() {
      return this.errorMessage;
    }

    @Override
    public void setErrorMessage(Message var1) {
      this.errorMessage = var1;
    }

    public void clearAll() {
      proxyRequestMessage = null;
      proxyResponseMessage = null;
      targetRequestMessage = null;
      targetResponseMessage = null;
    }
  }

  abstract static class FakeMessage implements Message {

    String content;
    Map<String, List<String>> headers;
    Map<String, List<String>> queryParams;
    Map<String, Object> variables;

    @Override
    public Set<String> getHeaderNames() {
      if (headers == null) {
        return null;
      }
      return headers.keySet();
    }

    @Override
    public List<String> getHeaders(String var1) {
      if (headers == null) {
        return null;
      }
      return headers.getOrDefault(var1, null);
    }

    @Override
    public boolean setHeader(String var1, Object var2) {
      if (headers == null) {
        headers = new HashMap<>();
      }
      headers.putIfAbsent(var1, new ArrayList<>());
      headers.get(var1).add((String) var2);
      return true;
    }

    @Override
    public String getContent() {
      return content;
    }

    @Override
    public void setContent(String var1) {
      content = var1;
    }

    @Override
    public Object getVariable(String var1) {
      if (variables == null) {
        return null;
      }
      return variables.getOrDefault(var1, null);
    }

    @Override
    public boolean setVariable(String var1, Object var2) {
      if (variables == null) {
        variables = new HashMap<>();
      }
      variables.put(var1, var2);
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
    public List<String> getQueryParams(String var1) {
      if (queryParams == null) {
        return null;
      }
      return queryParams.getOrDefault(var1, null);
    }

    @Override
    public boolean setQueryParam(String var1, Object var2) {
      if (queryParams == null) {
        queryParams = new HashMap<>();
      }
      queryParams.putIfAbsent(var1, new ArrayList<>());
      queryParams.get(var1).add((String) var2);
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
}

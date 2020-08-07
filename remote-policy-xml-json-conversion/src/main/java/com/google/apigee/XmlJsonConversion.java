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

import com.google.apigee.Execute.Execution;
import com.google.apigee.Execute.ExecutionResult.Action;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.json.JSONObject;
import org.json.XML;

/**
 * Decodes an Execution Protocol Buffer Message and converts Target Request Message content from XML
 * to JSON or vice versa depending on specified flow variable. Designed to be deployed as a Google
 * Cloud Function.
 */
public class XmlJsonConversion implements HttpFunction {

  private final static String CONVERSION_FLOW_VARIABLE = "conversion";
  private final static String XML_TO_JSON = "xmltojson";
  private final static String JSON_TO_XML = "jsontoxml";

  /**
   * Reads and decodes the Execution Protocol Buffer Message and converts XML to JSON or vice versa
   * depending on specified flow variable.
   *
   * @param request  contents of HTTP request containing the Protocol Buffer Message bytes
   * @param response HttpResponse sent in response to Http request containing modified Protocol
   *                 Buffer Message with new flow variable set
   * @throws IOException
   */
  @Override
  public void service(HttpRequest request, HttpResponse response) throws IOException {
    try {
      InputStream is = request.getInputStream();
      byte[] data = is.readAllBytes();
      Execute.Execution execution = Execute.Execution.parseFrom(data);
      execution = convert(execution);
      execution = setExecutionResult(execution, Action.CONTINUE, null);
      response.getOutputStream().write(execution.toByteArray());
    } catch (Throwable throwable) {
      Execute.Execution execution = Execution.getDefaultInstance();
      execution = setExecutionResult(execution, Action.ABORT, throwable);
      response.getOutputStream().write(execution.toByteArray());
    }
  }

  /**
   * Delegates conversion of XML to JSON or JSON to XML depending on CONVERSION_FLOW_VARIABLE
   * value.
   *
   * @param execution Execute.Execution object from caller.
   * @return Execute.Execution object with converted content inside Target Request Message.
   * @throws Exception
   */
  private Execute.Execution convert(Execute.Execution execution)
      throws UnsupportedEncodingException {
    validateExecution(execution);
    String conversion = execution.getMessageContext().getTargetRequestMessage()
        .getFlowVariablesMap().get(CONVERSION_FLOW_VARIABLE).getFlowVariable();
    String content = execution.getMessageContext().getTargetRequestMessage().getContent()
        .toString("UTF-8");
    if (XML_TO_JSON.equals(conversion)) {
      execution = xmlToJson(execution, content);
    } else if (JSON_TO_XML.equals(conversion)) {
      execution = jsonToXml(execution, content);
    }
    return execution;
  }

  /**
   * Converts XML content to JSON.
   *
   * @param execution Execute.Execution object to write to.
   * @param content   String content to convert.
   * @return Execute.Execution object with converted content.
   * @throws UnsupportedEncodingException
   */
  private Execute.Execution xmlToJson(Execute.Execution execution, String content)
      throws UnsupportedEncodingException {
    Execute.Execution.Builder executionBuilder = execution.toBuilder();
    JSONObject jsonObject = XML.toJSONObject(content);
    executionBuilder.getMessageContextBuilder().getTargetRequestMessageBuilder()
        .setContent(ByteString.copyFrom(jsonObject.toString(), "UTF-8"));
    return executionBuilder.build();
  }

  /**
   * Converts JSON content to XML.
   *
   * @param execution Execute.Execution object to write to.
   * @param content   String content to convert.
   * @return Execute.Execution object with converted content.
   * @throws UnsupportedEncodingException
   */
  private Execute.Execution jsonToXml(Execute.Execution execution, String content)
      throws UnsupportedEncodingException {
    Execute.Execution.Builder executionBuilder = execution.toBuilder();
    JSONObject jsonObject = new JSONObject(content);
    String xml = XML.toString(jsonObject);
    executionBuilder.getMessageContextBuilder().getTargetRequestMessageBuilder()
        .setContent(ByteString.copyFrom(xml, "UTF-8"));
    return executionBuilder.build();
  }

  /**
   * Validates that Execute.Execution object contains MessageContext and target request message.
   * Throws {@link IllegalArgumentException} if required fields are missing.
   *
   * @param execution Execute.Execution object to validate.
   */
  private void validateExecution(Execute.Execution execution) {
    if (!execution.hasMessageContext()) {
      throw new IllegalArgumentException("missing MessageContext");
    }
    if (!execution.getMessageContext().hasTargetRequestMessage()) {
      throw new IllegalArgumentException("missing target_request_message");
    }
  }

  /**
   * Sets the Execute.ExecutionResult object based on given Action and nullable Throwable.
   *
   * @param execution Execute.Execution object to set ExecutionResult in.
   * @param action    Action enumerator to set in ExecutionResult.
   * @param throwable Nullable Throwable object for ExecutionResult.
   * @return Execute.Execution object with ExecutionResult set.
   */
  private Execute.Execution setExecutionResult(Execute.Execution execution,
      Execute.ExecutionResult.Action action, Throwable throwable) {
    Execute.Execution.Builder executionBuilder = execution.toBuilder();
    Execute.ExecutionResult.Builder resultBuilder = executionBuilder.getExecutionResultBuilder();
    resultBuilder.setAction(action);
    if (throwable != null) {
      resultBuilder.setErrorResponse(throwable.toString());
    }
    return executionBuilder.build();
  }
}


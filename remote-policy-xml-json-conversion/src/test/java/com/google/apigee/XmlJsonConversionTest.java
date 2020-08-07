package com.google.apigee;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.google.apigee.Execute.Execution;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.protobuf.TextFormat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class XmlJsonConversionTest {

  private static final String CONTENT_JSON = "{\\\"sample\\\":{\\\"heading\\\":\\\"heading\\\",\\\"body\\\":\\\"message\\\"}}";
  private static final String CONTENT_XML = "<sample><heading>heading</heading><body>message</body></sample>";
  private static final String CONVERSION_KEY = "conversion";
  private static final String CONVERSION_VALUE_JSON_TO_XML = "jsontoxml";
  private static final String CONVERSION_VALUE_XML_TO_JSON = "xmltojson";
  private XmlJsonConversion XMLJsonConversion;
  @Mock private HttpRequest httpRequest;
  @Mock private HttpResponse httpResponse;
  @Mock private ByteArrayInputStream inputStream;
  private ByteArrayOutputStream byteArrayOutputStream;

  @Before
  public void init() throws Exception {
    MockitoAnnotations.openMocks(this);

    XMLJsonConversion = new XmlJsonConversion();
    doReturn(inputStream).when(httpRequest).getInputStream();
    byteArrayOutputStream = new ByteArrayOutputStream();
    doReturn(byteArrayOutputStream).when(httpResponse).getOutputStream();
  }

  @Test
  public void testServiceXmlToJson() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "    content: \"" + CONTENT_XML + "\""
            + "   flow_variables {"
            + "      key: \"" + CONVERSION_KEY + "\""
            + "      value {"
            + "        flow_variable: \"" + CONVERSION_VALUE_XML_TO_JSON + "\""
            + "      }"
            + "    }"
            + "  }"
            + "}"
            + "executionContext {"
            + "}",
        executionBuilder);

    doReturn(executionBuilder.build().toByteArray()).when(inputStream).readAllBytes();
    XMLJsonConversion.service(httpRequest, httpResponse);

    Execution.Builder expectedBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "    content: \"" + CONTENT_JSON + "\""
            + "    flow_variables {"
            + "      key: \"" + CONVERSION_KEY + "\""
            + "      value {"
            + "        flow_variable: \"" + CONVERSION_VALUE_XML_TO_JSON + "\""
            + "      }"
            + "    }"
            + "  }"
            + "}"
            + "executionContext {"
            + "}"
            + "executionResult {"
            + "    action: CONTINUE"
            + "}",
        expectedBuilder);

    assertEquals(
        expectedBuilder.build(),
        Execution.parseFrom(byteArrayOutputStream.toByteArray()).toBuilder().build());
  }

  @Test
  public void testServiceJsonToXml() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "    content: \"" + CONTENT_JSON + "\""
            + "   flow_variables {"
            + "      key: \"" + CONVERSION_KEY + "\""
            + "      value {"
            + "        flow_variable: \"" + CONVERSION_VALUE_JSON_TO_XML + "\""
            + "      }"
            + "    }"
            + "  }"
            + "}"
            + "executionContext {"
            + "}",
        executionBuilder);

    doReturn(executionBuilder.build().toByteArray()).when(inputStream).readAllBytes();
    XMLJsonConversion.service(httpRequest, httpResponse);

    Execution.Builder expectedBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "    content: \"" + CONTENT_XML + "\""
            + "    flow_variables {"
            + "      key: \"" + CONVERSION_KEY + "\""
            + "      value {"
            + "        flow_variable: \"" + CONVERSION_VALUE_JSON_TO_XML + "\""
            + "      }"
            + "    }"
            + "  }"
            + "}"
            + "executionContext {"
            + "}"
            + "executionResult {"
            + "    action: CONTINUE"
            + "}",
        expectedBuilder);

    assertEquals(
        expectedBuilder.build(),
        Execution.parseFrom(byteArrayOutputStream.toByteArray()).toBuilder().build());
  }

  @Test
  public void testServiceNoMessageContext() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "executionContext {"
            + "}",
        executionBuilder);

    doReturn(executionBuilder.build().toByteArray()).when(inputStream).readAllBytes();
    XMLJsonConversion.service(httpRequest, httpResponse);

    Execution.Builder expectedBuilder = Execution.newBuilder();
    TextFormat.merge(
        "executionResult {"
            + "    action: ABORT"
            + "    error_response: \"java.lang.IllegalArgumentException: missing MessageContext\""
            + "}",
        expectedBuilder);

    assertEquals(
        expectedBuilder.build(),
        Execution.parseFrom(byteArrayOutputStream.toByteArray()).toBuilder().build());
  }

  @Test
  public void testServiceNoTargetRequestMessage() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "}"
            + "executionContext {"
            + "}",
        executionBuilder);

    doReturn(executionBuilder.build().toByteArray()).when(inputStream).readAllBytes();
    XMLJsonConversion.service(httpRequest, httpResponse);

    Execution.Builder expectedBuilder = Execution.newBuilder();
    TextFormat.merge(
        "executionResult {"
            + "    action: ABORT"
            + "    error_response: \"java.lang.IllegalArgumentException: missing target_request_message\""
            + "}",
        expectedBuilder);

    assertEquals(
        expectedBuilder.build(),
        Execution.parseFrom(byteArrayOutputStream.toByteArray()).toBuilder().build());
  }

  @Test
  public void testServiceInvalidConversionFlowVariable() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "    content: \"" + CONTENT_JSON + "\""
            + "   flow_variables {"
            + "      key: \"" + CONVERSION_KEY + "\""
            + "      value {"
            + "        flow_variable: \"" + "invalid" + "\""
            + "      }"
            + "    }"
            + "  }"
            + "}"
            + "executionContext {"
            + "}",
        executionBuilder);

    doReturn(executionBuilder.build().toByteArray()).when(inputStream).readAllBytes();
    XMLJsonConversion.service(httpRequest, httpResponse);

    Execution.Builder expectedBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "    content: \"" + CONTENT_JSON + "\""
            + "   flow_variables {"
            + "      key: \"" + CONVERSION_KEY + "\""
            + "      value {"
            + "        flow_variable: \"" + "invalid" + "\""
            + "      }"
            + "    }"
            + "  }"
            + "}"
            + "executionContext {"
            + "}"
            + "executionResult {"
            + "    action: CONTINUE"
            + "}",
        expectedBuilder);

    assertEquals(
        expectedBuilder.build(),
        Execution.parseFrom(byteArrayOutputStream.toByteArray()).toBuilder().build());
  }

  @Test
  public void testServiceInvalidJson() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "    content: \"invalid\""
            + "   flow_variables {"
            + "      key: \"" + CONVERSION_KEY + "\""
            + "      value {"
            + "        flow_variable: \"" + CONVERSION_VALUE_JSON_TO_XML + "\""
            + "      }"
            + "    }"
            + "  }"
            + "}"
            + "executionContext {"
            + "}",
        executionBuilder);

    doReturn(executionBuilder.build().toByteArray()).when(inputStream).readAllBytes();
    XMLJsonConversion.service(httpRequest, httpResponse);

    Execution.Builder expectedBuilder = Execution.newBuilder();
    TextFormat.merge(
        "executionResult {\n"
            + "  action: ABORT\n"
            + "  error_response: \"org.json.JSONException: A JSONObject text must begin with \\'{\\' at 1 [character 2 line 1]\"\n"
            + "}",
        expectedBuilder);

    assertEquals(
        expectedBuilder.build(),
        Execution.parseFrom(byteArrayOutputStream.toByteArray()).toBuilder().build());
  }

  @Test
  public void testServiceInvalidXml() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "    content: \"0129<<<3>nsd\""
            + "   flow_variables {"
            + "      key: \"" + CONVERSION_KEY + "\""
            + "      value {"
            + "        flow_variable: \"" + CONVERSION_VALUE_XML_TO_JSON + "\""
            + "      }"
            + "    }"
            + "  }"
            + "}"
            + "executionContext {"
            + "}",
        executionBuilder);

    doReturn(executionBuilder.build().toByteArray()).when(inputStream).readAllBytes();
    XMLJsonConversion.service(httpRequest, httpResponse);

    Execution.Builder expectedBuilder = Execution.newBuilder();
    TextFormat.merge(
        "executionResult {\n"
            + "  action: ABORT\n"
            + "  error_response: \"org.json.JSONException: Misplaced \\'<\\' at 6 [character 7 line 1]\"\n"
            + "}",
        expectedBuilder);

    assertEquals(
        expectedBuilder.build(),
        Execution.parseFrom(byteArrayOutputStream.toByteArray()).toBuilder().build());
  }

  @Test
  public void testServiceException() throws Exception {
    doThrow(new IOException()).when(httpRequest).getInputStream();
    XMLJsonConversion.service(httpRequest, httpResponse);

    Execution.Builder expectedBuilder = Execution.newBuilder();
    TextFormat.merge(
        "executionResult {"
            + "    action: ABORT"
            + "    error_response: \"java.io.IOException\""
            + "}",
        expectedBuilder);

    assertEquals(
        expectedBuilder.build(),
        Execution.parseFrom(byteArrayOutputStream.toByteArray()).toBuilder().build());
  }
}

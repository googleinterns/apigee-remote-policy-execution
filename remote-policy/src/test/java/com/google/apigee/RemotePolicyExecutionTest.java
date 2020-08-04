package com.google.apigee;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import com.google.apigee.Execute.Execution;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.protobuf.TextFormat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RemotePolicyExecutionTest {

  private static final String KEY = "Example";
  private static final String VALUE = "Hello";
  private RemotePolicyExecution remotePolicyExecution;
  @Mock private HttpRequest httpRequest;
  @Mock private HttpResponse httpResponse;
  @Mock private ByteArrayInputStream inputStream;
  private ByteArrayOutputStream byteArrayOutputStream;

  @Before
  public void init() throws Exception {
    MockitoAnnotations.openMocks(this);

    remotePolicyExecution = new RemotePolicyExecution();
    doReturn(inputStream).when(httpRequest).getInputStream();
    byteArrayOutputStream = new ByteArrayOutputStream();
    doReturn(byteArrayOutputStream).when(httpResponse).getOutputStream();
  }

  @Test
  public void testService() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "  }"
            + "}"
            + "executionContext {"
            + "}",
        executionBuilder);

    doReturn(executionBuilder.build().toByteArray()).when(inputStream).readAllBytes();
    remotePolicyExecution.service(httpRequest, httpResponse);

    Execution.Builder expectedBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "    flow_variables {"
            + "      key: \"" + KEY + "\""
            + "      value {"
            + "        flow_variable: \"" + VALUE + "\""
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
}

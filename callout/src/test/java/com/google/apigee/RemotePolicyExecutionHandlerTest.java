package com.google.apigee;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import com.google.apigee.Execute.Execution;
import com.google.protobuf.TextFormat;
import java.io.ByteArrayInputStream;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RemotePolicyExecutionHandlerTest {

  private static final String KEY = "key";
  private static final String VALUE = "value";
  private RemotePolicyExecutionHandler remotePolicyExecutionHandler;
  @Mock private CloseableHttpClient httpClient;
  @Mock private HttpPost httpPost;
  @Mock private CloseableHttpResponse httpResponse;

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);

    remotePolicyExecutionHandler = new RemotePolicyExecutionHandler(httpClient, httpPost);
  }

  @Test
  public void testSendRequest() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {"
            + "  target_request_message {"
            + "  }"
            + "}",
        executionBuilder);
    Execution execution = executionBuilder.build();

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
            + "}",
        executionBuilder);
    Execution expected = expectedBuilder.build();

    doReturn(httpResponse).when(httpClient).execute(httpPost);
    doReturn(new InputStreamEntity(new ByteArrayInputStream(expected.toByteArray())))
        .when(httpResponse)
        .getEntity();

    Execute.Execution result =
        remotePolicyExecutionHandler.sendRemoteHttpServerRequest(execution, "");

    assertEquals(expected, result);
  }
}

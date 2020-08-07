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

  @Test
  public void testServiceException() throws Exception {
    doThrow(new IOException()).when(httpRequest).getInputStream();
    remotePolicyExecution.service(httpRequest, httpResponse);

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

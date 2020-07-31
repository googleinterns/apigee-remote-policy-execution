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

import com.apigee.flow.execution.Action;
import com.apigee.flow.execution.ExecutionResult;
import com.google.apigee.Execute;
import com.google.protobuf.TextFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;

public class ExecutionResultProtoMessageBuilderTest {

  private static final String KEY1 = "key1";
  private static final String KEY2 = "key2";
  private static final String VAL1 = "val1";
  private static final String VAL2 = "val2";
  private static final String ERROR_RESPONSE = "errorresponse1";

  @Test
  public void testBuildExecutionResultWithActionContinue() throws Exception {
    Execute.ExecutionResult.Builder expectedProtoBuilder = Execute.ExecutionResult.newBuilder();
    TextFormat.merge("action: CONTINUE", expectedProtoBuilder);
    Execute.ExecutionResult actualProto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(ExecutionResult.SUCCESS);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionResultWithActionPause() throws Exception {
    Execute.ExecutionResult.Builder expectedProtoBuilder = Execute.ExecutionResult.newBuilder();
    TextFormat.merge("action: PAUSE", expectedProtoBuilder);
    Execute.ExecutionResult actualProto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(ExecutionResult.PAUSE);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionResultWithActionAbort() throws Exception {
    Execute.ExecutionResult.Builder expectedProtoBuilder = Execute.ExecutionResult.newBuilder();
    TextFormat.merge("action: ABORT", expectedProtoBuilder);
    Execute.ExecutionResult actualProto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(ExecutionResult.ABORT);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionResultWithProperties() throws Exception {
    Execute.ExecutionResult.Builder expectedProtoBuilder = Execute.ExecutionResult.newBuilder();
    TextFormat.merge(
        "action: CONTINUE\n"
            + "properties {"
            + "  key: \""
            + KEY1
            + "\""
            + "  value: \""
            + VAL1
            + "\""
            + "}"
            + "properties {"
            + "  key: \""
            + KEY2
            + "\""
            + "  value: \""
            + VAL2
            + "\""
            + "}",
        expectedProtoBuilder);
    ExecutionResult executionResult = new ExecutionResult(true, Action.CONTINUE);
    Properties properties = new Properties();
    properties.put(KEY1, VAL1);
    properties.put(KEY2, VAL2);
    executionResult.setProperties(properties);
    Execute.ExecutionResult actualProto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(executionResult);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionResultWithErrorResponseHeaders() throws Exception {
    Execute.ExecutionResult.Builder expectedProtoBuilder = Execute.ExecutionResult.newBuilder();
    TextFormat.merge(
        "action: CONTINUE\n"
            + "error_response_headers {"
            + "  key: \""
            + KEY1
            + "\""
            + "  value: \""
            + VAL1
            + "\""
            + "}"
            + "error_response_headers {"
            + "  key: \""
            + KEY2
            + "\""
            + "  value: \""
            + VAL2
            + "\""
            + "}",
        expectedProtoBuilder);
    ExecutionResult executionResult = new ExecutionResult(true, Action.CONTINUE);
    Map<String, String> errorResponseHeaders = new HashMap<>();
    errorResponseHeaders.put(KEY1, VAL1);
    errorResponseHeaders.put(KEY2, VAL2);
    executionResult.setErrorResponseHeaders(errorResponseHeaders);
    Execute.ExecutionResult actualProto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(executionResult);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionResultWithErrorResponse() throws Exception {
    Execute.ExecutionResult.Builder expectedProtoBuilder = Execute.ExecutionResult.newBuilder();
    TextFormat.merge(
        "action: CONTINUE\n" + "error_response: \"" + ERROR_RESPONSE + "\"", expectedProtoBuilder);
    ExecutionResult executionResult = new ExecutionResult(true, Action.CONTINUE);
    executionResult.setErrorResponse(ERROR_RESPONSE);
    Execute.ExecutionResult actualProto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(executionResult);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }
}

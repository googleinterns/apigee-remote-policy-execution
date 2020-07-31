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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.junit.Test;

public class ExecutionResultProtoMessageBuilderTest {

  private static final String KEY1 = "key1";
  private static final String KEY2 = "key2";
  private static final String VAL1 = "val1";
  private static final String VAL2 = "val2";
  private static final String ERROR_RESPONSE = "errorresponse1";

  @Test
  public void testBuildExecutionResultWithActionContinue() {
    Execute.ExecutionResult proto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(ExecutionResult.SUCCESS);

    assertEquals(Execute.ExecutionResult.Action.CONTINUE, proto.getAction());
  }

  @Test
  public void testBuildExecutionResultWithActionPause() {
    Execute.ExecutionResult proto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(ExecutionResult.PAUSE);

    assertEquals(Execute.ExecutionResult.Action.PAUSE, proto.getAction());
  }

  @Test
  public void testBuildExecutionResultWithActionAbort() {
    Execute.ExecutionResult proto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(ExecutionResult.ABORT);

    assertEquals(Execute.ExecutionResult.Action.ABORT, proto.getAction());
  }

  @Test
  public void testBuildExecutionResultWithProperties() {
    ExecutionResult executionResult = new ExecutionResult(true, Action.CONTINUE);
    Properties properties = new Properties();
    properties.put(KEY1, VAL1);
    properties.put(KEY2, VAL2);
    executionResult.setProperties(properties);
    Execute.ExecutionResult proto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(executionResult);

    assertEquals(Execute.ExecutionResult.Action.CONTINUE, proto.getAction());
    assertEquals(2, proto.getPropertiesCount());
    assertEquals(
        properties.entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())),
        proto.getPropertiesMap());
  }

  @Test
  public void testBuildExecutionResultWithErrorResponseHeaders() {
    ExecutionResult executionResult = new ExecutionResult(true, Action.CONTINUE);
    Map<String, String> errorResponseHeaders = new HashMap<>();
    errorResponseHeaders.put(KEY1, VAL1);
    errorResponseHeaders.put(KEY2, VAL2);
    executionResult.setErrorResponseHeaders(errorResponseHeaders);
    Execute.ExecutionResult proto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(executionResult);

    assertEquals(Execute.ExecutionResult.Action.CONTINUE, proto.getAction());
    assertEquals(2, proto.getErrorResponseHeadersCount());
    assertEquals(errorResponseHeaders, proto.getErrorResponseHeadersMap());
  }

  @Test
  public void testBuildExecutionResultWithErrorResponse() {
    ExecutionResult executionResult = new ExecutionResult(true, Action.CONTINUE);
    executionResult.setErrorResponse(ERROR_RESPONSE);
    Execute.ExecutionResult proto =
        ExecutionResultProtoMessageBuilder.buildExecutionResultMessage(executionResult);

    assertEquals(Execute.ExecutionResult.Action.CONTINUE, proto.getAction());
    assertEquals(ERROR_RESPONSE, proto.getErrorResponse());
  }
}

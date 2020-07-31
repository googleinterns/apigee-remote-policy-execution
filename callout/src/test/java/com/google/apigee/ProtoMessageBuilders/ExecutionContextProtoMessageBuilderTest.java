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
import static org.mockito.Mockito.doReturn;

import com.apigee.flow.Fault;
import com.apigee.flow.execution.ExecutionContext;
import com.google.apigee.Execute;
import com.google.protobuf.TextFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class ExecutionContextProtoMessageBuilderTest {

  private static final String ATTRIBUTES_KEY1 = "key1";
  private static final String ATTRIBUTES_KEY2 = "key2";
  private static final String ATTRIBUTES_VALUE1 = "value1";
  private static final String ATTRIBUTES_VALUE2 = "value2";
  private static final String FAULT_NAME = "faultname";
  private static final String FAULT_REASON = "faultreason";
  private static final String FAULT_SUB_CATEGORY = "faultsubcategory";
  @Mock private Fault mockFault;
  @Spy private FakeExecutionContext executionContext;

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);

    doReturn(Fault.Category.Messaging).when(mockFault).getCategory();
    doReturn(FAULT_NAME).when(mockFault).getName();
    doReturn(FAULT_REASON).when(mockFault).getReason();
    doReturn(FAULT_SUB_CATEGORY).when(mockFault).getSubCategory();

    executionContext.setRequestFlow(false);
    executionContext.setErrorFlow(false);
    executionContext.clearFaults();
  }

  @Test
  public void testBuildExecutionContextProtoDefault() {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoFlowTypeRequest() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge("flow_type: REQUEST", expectedProtoBuilder);
    executionContext.setRequestFlow(true);
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoFlowTypeError() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge("flow_type: ERROR", expectedProtoBuilder);
    executionContext.setErrorFlow(true);
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoFlowTypeErrorAndRequest() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge("flow_type: ERROR", expectedProtoBuilder);
    executionContext.setErrorFlow(true);
    executionContext.setRequestFlow(true);
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoOneFault() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge(
        "faults {"
            + "  category: MESSAGING"
            + "  sub_category: \""
            + FAULT_SUB_CATEGORY
            + "\""
            + "  name: \""
            + FAULT_NAME
            + "\""
            + "  reason: \""
            + FAULT_REASON
            + "\""
            + "}",
        expectedProtoBuilder);
    executionContext.addFault(mockFault);
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoWithMultipleFaults() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge(
        "faults {"
            + "  category: MESSAGING"
            + "  sub_category: \""
            + FAULT_SUB_CATEGORY
            + "\""
            + "  name: \""
            + FAULT_NAME
            + "\""
            + "  reason: \""
            + FAULT_REASON
            + "\""
            + "}"
            + "faults {"
            + "  category: MESSAGING"
            + "  sub_category: \""
            + FAULT_SUB_CATEGORY
            + "\""
            + "  name: \""
            + FAULT_NAME
            + "\""
            + "  reason: \""
            + FAULT_REASON
            + "\""
            + "}",
        expectedProtoBuilder);
    executionContext.addFault(mockFault);
    executionContext.addFault(mockFault);
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultMessaging() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge(
        "faults {"
            + "  category: MESSAGING"
            + "  sub_category: \""
            + FAULT_SUB_CATEGORY
            + "\""
            + "  name: \""
            + FAULT_NAME
            + "\""
            + "  reason: \""
            + FAULT_REASON
            + "\""
            + "}",
        expectedProtoBuilder);
    executionContext.addFault(mockFault);
    doReturn(Fault.Category.Messaging).when(mockFault).getCategory();
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultStep() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge(
        "faults {"
            + "  category: STEP"
            + "  sub_category: \""
            + FAULT_SUB_CATEGORY
            + "\""
            + "  name: \""
            + FAULT_NAME
            + "\""
            + "  reason: \""
            + FAULT_REASON
            + "\""
            + "}",
        expectedProtoBuilder);
    executionContext.addFault(mockFault);
    doReturn(Fault.Category.Step).when(mockFault).getCategory();
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultTransport() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge(
        "faults {"
            + "  category: TRANSPORT"
            + "  sub_category: \""
            + FAULT_SUB_CATEGORY
            + "\""
            + "  name: \""
            + FAULT_NAME
            + "\""
            + "  reason: \""
            + FAULT_REASON
            + "\""
            + "}",
        expectedProtoBuilder);
    executionContext.addFault(mockFault);
    doReturn(Fault.Category.Transport).when(mockFault).getCategory();
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultSystem() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge(
        "faults {"
            + "  category: SYSTEM"
            + "  sub_category: \""
            + FAULT_SUB_CATEGORY
            + "\""
            + "  name: \""
            + FAULT_NAME
            + "\""
            + "  reason: \""
            + FAULT_REASON
            + "\""
            + "}",
        expectedProtoBuilder);
    executionContext.addFault(mockFault);
    doReturn(Fault.Category.System).when(mockFault).getCategory();
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultWithAttributes() throws Exception {
    Execute.ExecutionContext.Builder expectedProtoBuilder = Execute.ExecutionContext.newBuilder();
    TextFormat.merge(
        "faults {\n"
            + "  category: MESSAGING"
            + "  sub_category: \""
            + FAULT_SUB_CATEGORY
            + "\""
            + "  name: \""
            + FAULT_NAME
            + "\""
            + "  reason: \""
            + FAULT_REASON
            + "\""
            + "  attributes {"
            + "    key: \""
            + ATTRIBUTES_KEY1
            + "\""
            + "    value: \""
            + ATTRIBUTES_VALUE1
            + "\""
            + "  }"
            + "  attributes {"
            + "    key: \""
            + ATTRIBUTES_KEY2
            + "\""
            + "    value: \""
            + ATTRIBUTES_VALUE2
            + "\""
            + "  }"
            + "}",
        expectedProtoBuilder);
    executionContext.addFault(mockFault);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put(ATTRIBUTES_KEY1, ATTRIBUTES_VALUE1);
    attributes.put(ATTRIBUTES_KEY2, ATTRIBUTES_VALUE2);
    doReturn(attributes).when(mockFault).getAttributes();
    Execute.ExecutionContext actualProto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(expectedProtoBuilder.build(), actualProto);
  }

  abstract static class FakeExecutionContext implements ExecutionContext {

    private final List<Fault> faults = new ArrayList<>();
    private boolean errorFlow;
    private boolean requestFlow;

    @Override
    public boolean isRequestFlow() {
      return requestFlow;
    }

    public void setRequestFlow(boolean requestFlow) {
      this.requestFlow = requestFlow;
    }

    @Override
    public boolean isErrorFlow() {
      return errorFlow;
    }

    public void setErrorFlow(boolean errorFlow) {
      this.errorFlow = errorFlow;
    }

    @Override
    public Collection<Fault> getFaults() {
      return faults;
    }

    @Override
    public void addFault(Fault fault) {
      faults.add(fault);
    }

    public void clearFaults() {
      faults.clear();
    }
  }
}

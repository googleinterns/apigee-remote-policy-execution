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

import com.apigee.flow.Fault;
import com.apigee.flow.execution.ExecutionContext;
import com.google.apigee.Execute;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ExecutionContextProtoMessageBuilderTest {

  private final String ATTRIBUTES_KEY1 = "key1";
  private final String ATTRIBUTES_KEY2 = "key2";
  private final String ATTRIBUTES_VALUE1 = "value1";
  private final String ATTRIBUTES_VALUE2 = "value2";
  private final String FAULT_NAME = "faultname";
  private final String FAULT_REASON = "faultreason";
  private final String FAULT_SUB_CATEGORY = "faultsubcategory";
  Fault mockFault1 = mock(Fault.class);
  Fault mockFault2 = mock(Fault.class);
  @Spy private FakeExecutionContext executionContext;

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);

    doReturn(Fault.Category.Messaging).when(mockFault1).getCategory();
    doReturn(FAULT_NAME).when(mockFault1).getName();
    doReturn(FAULT_REASON).when(mockFault1).getReason();
    doReturn(null).when(mockFault1).getAttributes();
    doReturn(FAULT_SUB_CATEGORY).when(mockFault1).getSubCategory();
    doReturn(Fault.Category.Step).when(mockFault2).getCategory();
    doReturn(FAULT_NAME).when(mockFault2).getName();
    doReturn(FAULT_REASON).when(mockFault2).getReason();
    doReturn(null).when(mockFault2).getAttributes();
    doReturn(FAULT_SUB_CATEGORY).when(mockFault2).getSubCategory();

    executionContext.setRequestFlow(false);
    executionContext.setErrorFlow(false);
    executionContext.clearFaults();
  }

  @Test
  public void testBuildExecutionContextProtoDefault() {
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(0, proto.getFaultsCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoFlowTypeRequest() {
    executionContext.setRequestFlow(true);
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(0, proto.getFaultsCount());
    assertEquals(Execute.ExecutionContext.FlowType.REQUEST, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoFlowTypeError() {
    executionContext.setErrorFlow(true);
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(0, proto.getFaultsCount());
    assertEquals(Execute.ExecutionContext.FlowType.ERROR, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoFlowTypeBoth() {
    executionContext.setErrorFlow(true);
    executionContext.setRequestFlow(true);
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(0, proto.getFaultsCount());
    assertEquals(Execute.ExecutionContext.FlowType.ERROR, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoOneFault() throws Exception {
    executionContext.addFault(mockFault1);
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(
        Execute.ExecutionContext.Fault.Category.MESSAGING, proto.getFaults(0).getCategory());
    assertEquals(FAULT_SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(FAULT_REASON, proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithMultipleFaults() {
    executionContext.addFault(mockFault1);
    executionContext.addFault(mockFault2);
    doReturn(Fault.Category.Messaging).when(mockFault1).getCategory();
    doReturn(Fault.Category.Step).when(mockFault2).getCategory();
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(2, proto.getFaultsCount());
    assertEquals(
        Execute.ExecutionContext.Fault.Category.MESSAGING, proto.getFaults(0).getCategory());
    assertEquals(FAULT_SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(FAULT_REASON, proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.Fault.Category.STEP, proto.getFaults(1).getCategory());
    assertEquals(FAULT_SUB_CATEGORY, proto.getFaults(1).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(1).getName());
    assertEquals(FAULT_REASON, proto.getFaults(1).getReason());
    assertEquals(0, proto.getFaults(1).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultMessaging() {
    executionContext.addFault(mockFault1);
    doReturn(Fault.Category.Messaging).when(mockFault1).getCategory();
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(
        Execute.ExecutionContext.Fault.Category.MESSAGING, proto.getFaults(0).getCategory());
    assertEquals(FAULT_SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(FAULT_REASON, proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultStep() {
    executionContext.addFault(mockFault1);
    doReturn(Fault.Category.Step).when(mockFault1).getCategory();
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(Execute.ExecutionContext.Fault.Category.STEP, proto.getFaults(0).getCategory());
    assertEquals(FAULT_SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(FAULT_REASON, proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultTransport() {
    executionContext.addFault(mockFault1);
    doReturn(Fault.Category.Transport).when(mockFault1).getCategory();
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(
        Execute.ExecutionContext.Fault.Category.TRANSPORT, proto.getFaults(0).getCategory());
    assertEquals(FAULT_SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(FAULT_REASON, proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultSystem() {
    executionContext.addFault(mockFault1);
    doReturn(Fault.Category.System).when(mockFault1).getCategory();
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(Execute.ExecutionContext.Fault.Category.SYSTEM, proto.getFaults(0).getCategory());
    assertEquals(FAULT_SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(FAULT_REASON, proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultWithAttributes() {
    executionContext.addFault(mockFault1);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put(ATTRIBUTES_KEY1, ATTRIBUTES_VALUE1);
    attributes.put(ATTRIBUTES_KEY2, ATTRIBUTES_VALUE2);
    doReturn(Fault.Category.Messaging).when(mockFault1).getCategory();
    doReturn(attributes).when(mockFault1).getAttributes();
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(
        Execute.ExecutionContext.Fault.Category.MESSAGING, proto.getFaults(0).getCategory());
    assertEquals(FAULT_SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(FAULT_REASON, proto.getFaults(0).getReason());
    assertEquals(2, proto.getFaults(0).getAttributesCount());
    assertEquals(
        attributes.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue())),
        proto.getFaults(0).getAttributesMap());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  abstract static class FakeExecutionContext implements ExecutionContext {

    private final List<Fault> faults = new ArrayList<>();
    private boolean errorFlow;
    private boolean requestFlow;

    @Override
    public boolean isRequestFlow() {
      return requestFlow;
    }

    public void setRequestFlow(boolean val) {
      requestFlow = val;
    }

    @Override
    public boolean isErrorFlow() {
      return errorFlow;
    }

    public void setErrorFlow(boolean val) {
      errorFlow = val;
    }

    @Override
    public Collection<Fault> getFaults() {
      return faults;
    }

    @Override
    public void addFault(Fault var1) {

      faults.add(var1);
    }

    public void clearFaults() {
      faults.clear();
    }
  }
}

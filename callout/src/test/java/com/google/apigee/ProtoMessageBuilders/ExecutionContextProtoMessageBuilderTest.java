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
import com.apigee.flow.execution.Callback;
import com.apigee.flow.execution.ExecutionContext;
import com.google.apigee.Execute;
import com.google.protobuf.ByteString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Marker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Fault.class)
public class ExecutionContextProtoMessageBuilderTest {

  private final String ATTRIBUTES_KEY1 = "key1";
  private final String ATTRIBUTES_KEY2 = "key2";
  private final String ATTRIBUTES_VALUE1 = "value1";
  private final String ATTRIBUTES_VALUE2 = "value2";
  private final String FAULT_NAME = "Unchecked";
  private final String SUB_CATEGORY = "testsubcategory";
  private final Exception testException = new Exception("testexceptionmessage");

  private FakeExecutionContext executionContext = new FakeExecutionContext();

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);

    // Otherwise tests will fail due to java.lang.NoClassDefFoundError:
    // com/apigee/kernel/exceptions/spi/UncheckedException in Fault
    PowerMockito.stub(PowerMockito.method(Fault.class, "getFaultNameFromCause", Throwable.class))
        .toReturn(FAULT_NAME);

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
    executionContext.addFault(new Fault(Fault.Category.Messaging, SUB_CATEGORY, testException));
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(
        Execute.ExecutionContext.Fault.Category.MESSAGING, proto.getFaults(0).getCategory());
    assertEquals(SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(testException.getMessage(), proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithMultipleFaults() {
    executionContext.addFault(new Fault(Fault.Category.Messaging, SUB_CATEGORY, testException));
    executionContext.addFault(new Fault(Fault.Category.Step, SUB_CATEGORY, testException));
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(2, proto.getFaultsCount());

    assertEquals(
        Execute.ExecutionContext.Fault.Category.MESSAGING, proto.getFaults(0).getCategory());
    assertEquals(SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(testException.getMessage(), proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.Fault.Category.STEP, proto.getFaults(1).getCategory());
    assertEquals(SUB_CATEGORY, proto.getFaults(1).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(1).getName());
    assertEquals(testException.getMessage(), proto.getFaults(1).getReason());
    assertEquals(0, proto.getFaults(1).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultMessaging() {
    executionContext.addFault(new Fault(Fault.Category.Messaging, SUB_CATEGORY, testException));
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(
        Execute.ExecutionContext.Fault.Category.MESSAGING, proto.getFaults(0).getCategory());
    assertEquals(SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(testException.getMessage(), proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultStep() {
    executionContext.addFault(new Fault(Fault.Category.Step, SUB_CATEGORY, testException));
    System.out.println(new Fault(Fault.Category.Step, SUB_CATEGORY, testException).getReason());
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(Execute.ExecutionContext.Fault.Category.STEP, proto.getFaults(0).getCategory());
    assertEquals(SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(testException.getMessage(), proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultTransport() {
    executionContext.addFault(new Fault(Fault.Category.Transport, SUB_CATEGORY, testException));
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(
        Execute.ExecutionContext.Fault.Category.TRANSPORT, proto.getFaults(0).getCategory());
    assertEquals(SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(testException.getMessage(), proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultSystem() {
    executionContext.addFault(new Fault(Fault.Category.System, SUB_CATEGORY, testException));
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(Execute.ExecutionContext.Fault.Category.SYSTEM, proto.getFaults(0).getCategory());
    assertEquals(SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(testException.getMessage(), proto.getFaults(0).getReason());
    assertEquals(0, proto.getFaults(0).getAttributesCount());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  @Test
  public void testBuildExecutionContextProtoWithFaultWithAttributes() {
    Fault fault = new Fault(Fault.Category.Messaging, SUB_CATEGORY, testException);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put(ATTRIBUTES_KEY1, ATTRIBUTES_VALUE1);
    attributes.put(ATTRIBUTES_KEY2, ATTRIBUTES_VALUE2);
    fault.setAttributes(attributes);
    executionContext.addFault(fault);
    Execute.ExecutionContext proto =
        ExecutionContextProtoMessageBuilder.buildExecutionContextProto(executionContext);

    assertEquals(1, proto.getFaultsCount());
    assertEquals(
        Execute.ExecutionContext.Fault.Category.MESSAGING, proto.getFaults(0).getCategory());
    assertEquals(SUB_CATEGORY, proto.getFaults(0).getSubCategory());
    assertEquals(FAULT_NAME, proto.getFaults(0).getName());
    assertEquals(testException.getMessage(), proto.getFaults(0).getReason());
    assertEquals(2, proto.getFaults(0).getAttributesCount());
    assertEquals(buildAttributesMapForProto(attributes), proto.getFaults(0).getAttributesMap());
    assertEquals(Execute.ExecutionContext.FlowType.UNKNOWN_FLOW_TYPE, proto.getFlowType());
  }

  private Map<String, ByteString> buildAttributesMapForProto(Map<String, Object> map) {
    Map<String, ByteString> attributesMap = new HashMap<>();
    map.forEach(
        (key, value) -> {
          try {
            attributesMap.put(key, ByteString.copyFrom(objectToByteArray(value)));
          } catch (IOException exception) {
            throw new UncheckedIOException(exception);
          }
        });
    return attributesMap;
  }

  private byte[] objectToByteArray(Object obj) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    objectOutputStream.writeObject(obj);
    return byteArrayOutputStream.toByteArray();
  }

  static class FakeExecutionContext implements ExecutionContext {

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

    @Override
    public Marker getMarker() {
      return null;
    }

    @Override
    public void submitTask(Runnable runnable) {}

    @Override
    public void scheduleTask(Runnable runnable, long l, TimeUnit timeUnit) {}

    @Override
    public void submitTask(Runnable runnable, Callback callback, Object o) {}

    @Override
    public void resume() {}

    @Override
    public void resume(Fault fault) {}

    @Override
    public Fault getFault() {
      return null;
    }
  }
}

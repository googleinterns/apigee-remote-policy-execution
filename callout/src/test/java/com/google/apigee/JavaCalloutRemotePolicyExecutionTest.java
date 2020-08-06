package com.google.apigee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.apigee.flow.execution.Action;
import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.Message;
import com.apigee.flow.message.MessageContext;
import com.google.apigee.Execute.Execution;
import com.google.protobuf.TextFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JavaCalloutRemotePolicyExecutionTest {
  private static final String KEY = "Example";
  private static final String VALUE = "Value";
  private static final String URL_PROPERTY_NAME = "remote_execution_url";
  private static final String SERVER_URL = "url";
  @Mock private MessageContext messageContext;
  @Mock private ExecutionContext executionContext;
  @Mock private RemotePolicyExecutionHandler remotePolicyExecutionHandler;
  @Mock private Message message;
  private JavaCalloutRemotePolicyExecution callout;

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);

    Map<String, String> properties = new HashMap<>();
    properties.put(URL_PROPERTY_NAME, SERVER_URL);
    callout = new JavaCalloutRemotePolicyExecution(properties, remotePolicyExecutionHandler);
  }

  @Test
  public void testExecutionContinue() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
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
        executionBuilder);
    Execution execution = executionBuilder.build();

    Execution.Builder mockExecutionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {\n"
            + "}\n"
            + "executionContext {\n"
            + "}", mockExecutionBuilder);
    Execution mockExecution = mockExecutionBuilder.build();

    ExecutionResult expected = new ExecutionResult(true, Action.CONTINUE);
    expected.setErrorResponse("");
    expected.setErrorResponseHeaders(new HashMap<>());
    expected.setProperties(new Properties());

    doReturn(execution)
        .when(remotePolicyExecutionHandler)
        .sendRemoteHttpServerRequest(mockExecution, SERVER_URL);
    doReturn(message).when(messageContext).getMessage();
    doNothing().when(message).setContent(VALUE);

    ExecutionResult actual = callout.execute(messageContext, executionContext);

    verify(message).setContent(VALUE);

    assertTrue(isSameExecutionResult(expected, actual));
  }

  @Test
  public void testExecutionPause() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
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
            + "    action: PAUSE"
            + "}",
        executionBuilder);
    Execution execution = executionBuilder.build();

    Execution.Builder mockExecutionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {\n"
            + "}\n"
            + "executionContext {\n"
            + "}", mockExecutionBuilder);
    Execution mockExecution = mockExecutionBuilder.build();

    ExecutionResult expected = new ExecutionResult(true, Action.PAUSE);
    expected.setErrorResponse("");
    expected.setErrorResponseHeaders(new HashMap<>());
    expected.setProperties(new Properties());

    doReturn(execution)
        .when(remotePolicyExecutionHandler)
        .sendRemoteHttpServerRequest(mockExecution, SERVER_URL);
    doReturn(message).when(messageContext).getMessage();

    ExecutionResult actual = callout.execute(messageContext, executionContext);

    assertTrue(isSameExecutionResult(expected, actual));
  }

  @Test
  public void testExecutionAbort() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
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
            + "    action: ABORT"
            + "}",
        executionBuilder);
    Execution execution = executionBuilder.build();

    Execution.Builder mockExecutionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {\n"
            + "}\n"
            + "executionContext {\n"
            + "}", mockExecutionBuilder);
    Execution mockExecution = mockExecutionBuilder.build();

    ExecutionResult expected = new ExecutionResult(true, Action.ABORT);
    expected.setErrorResponse("");
    expected.setErrorResponseHeaders(new HashMap<>());
    expected.setProperties(new Properties());

    doReturn(execution)
        .when(remotePolicyExecutionHandler)
        .sendRemoteHttpServerRequest(mockExecution, SERVER_URL);
    doReturn(message).when(messageContext).getMessage();

    ExecutionResult actual = callout.execute(messageContext, executionContext);

    assertTrue(isSameExecutionResult(expected, actual));
  }

  @Test
  public void testExecutionNoExecutionResult() throws Exception {
    Execution.Builder executionBuilder = Execution.newBuilder();
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
            + "}",
        executionBuilder);
    Execution execution = executionBuilder.build();

    Execution.Builder mockExecutionBuilder = Execution.newBuilder();
    TextFormat.merge(
        "messageContext {\n"
            + "}\n"
            + "executionContext {\n"
            + "}", mockExecutionBuilder);
    Execution mockExecution = mockExecutionBuilder.build();

    ExecutionResult expected = ExecutionResult.ABORT;

    doReturn(execution)
        .when(remotePolicyExecutionHandler)
        .sendRemoteHttpServerRequest(mockExecution, SERVER_URL);
    doReturn(message).when(messageContext).getMessage();

    ExecutionResult actual = callout.execute(messageContext, executionContext);

    assertEquals(expected, actual);
  }

  private boolean isSameExecutionResult(ExecutionResult er1, ExecutionResult er2) {
    return er1.getAction() == er2.getAction()
        && er1.getErrorResponse().equals(er2.getErrorResponse())
        && er1.getProperties().equals(er2.getProperties())
        && er1.getErrorResponseHeaders().equals(er2.getErrorResponseHeaders());
  }
}

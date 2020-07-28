package com.google.apigee;

import com.apigee.flow.MessageFlowImpl;
import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionContextImpl;
import com.apigee.flow.message.FlowContext;
import com.apigee.flow.message.Message;
import com.apigee.flow.message.MessageContext;
import com.apigee.flow.message.MessageContextImpl;
import com.apigee.flow.message.MessageImpl;
import com.apigee.flow.message.TransportMessageFactory;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.protobuf.ProtoHttpContent;
import com.google.auth.Credentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/** Handles the call to Cloud Functions and returns the response. */
public class RemotePolicyExecutionHandler {
  /**
   * Sends an HTTP Request to the provided Cloud Functions URL with the serialized Execution
   * Protocol Buffer Message as content and returns the response.
   *
   * @param executionProtoMessage Execution Protocol Buffer Message to serialize and send to Cloud
   *     Functions.
   * @param urlString String URL of the Cloud Functions endpoint to hit.
   * @return Result of the Cloud Function call
   * @throws IOException
   */
  public String sendCloudFunctionsRequest(
      Execute.Execution executionProtoMessage, String urlString) throws IOException {
//    Credentials credentials =
//        ServiceAccountCredentials.fromStream(new FileInputStream(new File("/usr/local/google/home/morganklaw/workspace/apigee-remote-policy-execution/cloud-function/iconic-computer-279721-1c0f8118cd6c.json")))
//            .createScoped("https://www.googleapis.com/auth/cloud-platform");
//
//    IdTokenCredentials idTokenCredentials =
//        IdTokenCredentials.newBuilder()
//            .setIdTokenProvider((ServiceAccountCredentials) credentials)
//            .setTargetAudience(urlString)
//            .build();
//
//    HttpRequestFactory factory =
//        new NetHttpTransport().createRequestFactory(new HttpCredentialsAdapter(idTokenCredentials));
//    HttpRequest request = factory.buildGetRequest(new GenericUrl(urlString));
//    HttpContent content = new ProtoHttpContent(executionProtoMessage);
//    request.setContent(content);
//    HttpResponse httpResponse = request.execute();
//
//    return CharStreams.toString(new InputStreamReader(httpResponse.getContent(), Charsets.UTF_8));
    return "";
  }
}

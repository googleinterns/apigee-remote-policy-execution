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

import java.io.IOException;
import java.net.URI;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/** Handles the call to a remote HTTP Server and returns the response. */
class RemotePolicyExecutionHandler {
  private CloseableHttpClient httpClient;
  private HttpPost httpRequest;

  public RemotePolicyExecutionHandler() {
    this(HttpClients.createDefault(), new HttpPost());
  }

  public RemotePolicyExecutionHandler(CloseableHttpClient httpClient, HttpPost httpRequest) {
    this.httpClient = httpClient;
    this.httpRequest = httpRequest;
  }
  /**
   * Sends an HTTP Request to the provided URL with the serialized Execution Protocol Buffer
   * Message. Remote HTTP Server sets a Message flow variable that this function returns.
   *
   * @param executionProtoMessage Execution Protocol Buffer Message to serialize and send.
   * @param urlString String URL of the HTTP Server endpoint to hit.
   * @return Result of the remote HTTP call
   * @throws IOException
   */
  public Execute.Execution sendRemoteHttpServerRequest(
      Execute.Execution executionProtoMessage, String urlString) throws IOException {
    httpRequest.setURI(URI.create(urlString));
    httpRequest.setEntity(new ByteArrayEntity(executionProtoMessage.toByteArray()));
    httpRequest.setHeader("content-type", "application/octet-stream");
    try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
      byte[] responseContent = EntityUtils.toByteArray(response.getEntity());
      return Execute.Execution.parseFrom(responseContent);
    } finally {
      httpClient.close();
    }
  }
}

package presentation.http;

import java.util.LinkedHashMap;
import java.util.Map;
public class HttpResponse {
  private int statusCode;
  private String statusMessage;
  private LinkedHashMap<String, String> headers;
  private byte[] body;
  public HttpResponse() {
    this.headers = new LinkedHashMap<>();
    this.statusCode = 200;
    this.statusMessage = "OK";
    this.body = new byte[0];
  }

  public void setStatus (int statusCode, String statusMessage) {
    this.statusCode = statusCode;
    this.statusMessage = statusMessage;
  }

  public void addHeader(String key, String value) {
    this.headers.put(key, value);
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

  public String generateResponseHeaders() {
    StringBuilder response = new StringBuilder();
    response.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");
    
    for(Map.Entry<String, String> header : headers.entrySet()) {
      response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
    }

    response.append("\r\n");
    return response.toString();
  }

  public byte[] generateResponse() {
    String headers = generateResponseHeaders();
    byte[] headersBytes = headers.getBytes();
    byte[] fullResponse = new byte[headersBytes.length + body.length];

    System.arraycopy(headersBytes, 0, fullResponse, 0, headersBytes.length);
    System.arraycopy(body, 0, fullResponse, headersBytes.length, body.length);
    return fullResponse;
  }
}

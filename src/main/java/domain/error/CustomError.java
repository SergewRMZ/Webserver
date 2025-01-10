package domain.error;

import presentation.http.HttpResponse;

public class CustomError {
  public static HttpResponse badRequest(String message) {
    HttpResponse response = new HttpResponse();
    response.setStatus(400, "Bad Request");
    response.generateResponseHeaders();
    return response;
  }

  public static HttpResponse internalServer(String message) {
    HttpResponse response = new HttpResponse();
    response.setStatus(500, "Internal Server Error");
    response.generateResponseHeaders();
    return response;
  }
}

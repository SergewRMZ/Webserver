package domain.error;

import com.google.gson.JsonObject;

import presentation.http.HttpResponse;

public class CustomError {
  public static HttpResponse badRequest(String message) {
    JsonObject jsonResponse = new JsonObject();
    jsonResponse.addProperty("status", "error");
    jsonResponse.addProperty("message", message);
    
    HttpResponse response = new HttpResponse();
    response.setStatus(400, "Bad Request");
    response.addHeader("Content-Type", "application/json");
    
    response.addHeader("Content-Length", String.valueOf(jsonResponse.toString().getBytes().length));
    response.setBody(jsonResponse.toString().getBytes());
    return response;
  }
 
  public static HttpResponse internalServer(String message) {
    HttpResponse response = new HttpResponse();
    response.setStatus(500, "Internal Server Error");
    response.addHeader("Content-Type", "application/json");
    
    JsonObject jsonResponse = new JsonObject();
    jsonResponse.addProperty("status", "error");
    jsonResponse.addProperty("message", message);
    
    response.addHeader("Content-Length", String.valueOf(jsonResponse.toString().getBytes().length));
    
    response.setBody(jsonResponse.toString().getBytes());
    return response;
  }
}

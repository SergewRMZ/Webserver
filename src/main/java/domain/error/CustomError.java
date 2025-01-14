package domain.error;

import java.io.File;
import java.io.FileInputStream;

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

  public static HttpResponse notFound() {
    HttpResponse response = new HttpResponse();
    response.setStatus(404, "Recurso no encontrado");
    response.addHeader("Content-Type", "text/html");
    byte[] fileContent = null;
    File file = new File("public/NotFound.html");
    try (FileInputStream flujo = new FileInputStream(file)){
      fileContent = flujo.readAllBytes();
    } catch (Exception e) {
      System.err.println("Error al leer el archivo");
      e.printStackTrace();
    }
    response.addHeader("Content-Length", String.valueOf(fileContent.length));
    response.setBody(fileContent);
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

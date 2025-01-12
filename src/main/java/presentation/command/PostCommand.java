package presentation.command;
import java.io.FileOutputStream;
import java.util.Base64;

import com.google.gson.JsonObject;

import presentation.http.HttpResponse;
import presentation.services.FileService;

public class PostCommand implements Command {
  private String headers;
  private String basePath;
  private byte[] body;
  private FileService fileService;
  
  public PostCommand(String headers, byte[] body) {
    this.headers = headers;
    this.body = body;
    this.basePath = "public";
    this.fileService = new FileService();
  }

  @Override
  public HttpResponse execute() {
    if(headers.contains("/api/upload/static")) return handleApiRequest();
    return null;
  }

  private HttpResponse handleApiRequest() {
    HttpResponse response = new HttpResponse();
    try {
      String ruta = headers.split(" ")[1];
      String directory = basePath + ruta.replaceFirst("/api/upload", "");
      String filename = "uploaded_file.jpg";
      System.out.println(headers);
      System.out.println("Bytes: " + body.length);
     
      fileService.createDirectoryIfNotExist(directory);
      try (FileOutputStream fos = new FileOutputStream(directory + "/" + filename)) {
        fos.write(body);
      }

      response.setStatus(201, "Created");
      response.addHeader("Content-Type", "application/json");
      String jsonResponse = "{\"status\": \"success\", \"message\": \"Archivo subido exitosamente\"}";
      response.addHeader("Content-Length", String.valueOf(jsonResponse.getBytes().length));
      response.setBody(jsonResponse.getBytes());
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      response.setStatus(500, "Internal Server Error");
      JsonObject jsonRespose = new JsonObject();
      jsonRespose.addProperty("status", "error");
      jsonRespose.addProperty("message", "Error al subir el archivo");
      response.setBody(jsonRespose.toString().getBytes());
    }

    return response;
  }

  private byte[] extractFileData(String part) {
    String[] lines = part.split("\r\n\r\n");
    if(lines.length > 1) {
      return lines[1].getBytes();
    }

    return new byte[0];
  }
}

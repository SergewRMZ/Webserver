package presentation.command;
import java.io.File;
import java.io.FileOutputStream;

import domain.error.CustomError;
import presentation.http.HttpResponse;
import presentation.http.HttpUtils;
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

  // private HttpResponse handleApiRequest() {
  //   HttpResponse response = new HttpResponse();
  //   try {
  //     String ruta = headers.split(" ")[1];
  //     String directory = basePath + ruta.replaceFirst("/api/upload", "");
  //     String filename = "uploaded_file.jpg";
  //     System.out.println(headers);
  //     System.out.println("Bytes: " + body.length);
     
  //     fileService.createDirectoryIfNotExist(directory);
  //     try (FileOutputStream fos = new FileOutputStream(directory + "/" + filename)) {
  //       fos.write(body);
  //     }

      
  //     return response.createSuccessResponse(201, "created", "Archivo subido exitósamente");
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //     response.setStatus(500, "Internal Server Error");
  //     JsonObject jsonRespose = new JsonObject();
  //     jsonRespose.addProperty("status", "error");
  //     jsonRespose.addProperty("message", "Error al subir el archivo");
  //     response.setBody(jsonRespose.toString().getBytes());
  //   }

  //   return response;
  // }

  private HttpResponse handleApiRequest() {
    HttpResponse response = new HttpResponse();
    try {
      String ruta = headers.split(" ")[1];
      if(ruta.startsWith("/api/upload/static")) {
        String contentType = HttpUtils.getContentType(headers);
        String boundary = HttpUtils.getBoundary(contentType);
        String directory = basePath + ruta.replace("/api/upload", "");
        System.out.println(headers);
        String bodyAux = new String(body);
        System.out.println("Long body string: " + bodyAux.length());
        System.out.println("Long body en bytes: " + body.length);
        String[] parts = new String(body).split("--" + boundary);
        for(String part: parts) {
          System.out.println("Parte");
          if(part.contains("Content-Disposition")) {
            // String filename = part.split(";")[2].split("filename=")[1].replaceAll("[\\\"\\/:*?<>|]", "").trim();
            String filename = "Archivo.ts";
            byte[] fileData = extractFileData(part);
            fileService.createDirectoryIfNotExist(directory);
            File outputFile = new File(directory + File.separator + filename);
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(fileData);
            fos.close();
          }
        }

        return response.createSuccessResponse(201, "Created", "Archivo subido correctamente");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return CustomError.internalServer("Internal Server Error: No se pudo subir el archivo");
    }

    return null;
  }

  private byte[] extractFileData(String part) {
    String delimiter = "\r\n\r\n";
    int index = part.indexOf(delimiter);

    if (index != -1) {
        String fileData = part.substring(index + delimiter.length());
        
        fileData = fileData.trim();

        return fileData.getBytes();
    }

    return new byte[0];
  }
}

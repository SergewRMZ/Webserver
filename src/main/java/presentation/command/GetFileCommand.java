package presentation.command;

import java.io.File;
import java.io.FileInputStream;

import dto.MIME_TYPES;
import presentation.http.HttpResponse;
import presentation.services.FileService;

public class GetFileCommand implements Command {
  private String resource;
  private String basePath;
  private FileService fileService;

  public GetFileCommand(String resource) {
    this.resource = resource;
    this.basePath = "public";
    this.fileService = new FileService();
  }

  @Override
  public HttpResponse execute() {
    if(resource.startsWith("/api/files")) return handleApiRequest();
    else return handleStaticRequest(resource);
  }

  private HttpResponse handleApiRequest() {
    System.out.println("Recurso: " + resource);
    String directoryPath = resource.replaceFirst("/api/files/", "");
    String file = directoryPath.replaceFirst("static", "");
    System.out.println("Directorio solicitado: " + file);
    return fileService.getFiles("public/static" + file);
  }

  private HttpResponse handleStaticRequest(String resource) {
    String filepath = basePath + resource;
    if(resource.equals("/")) filepath = basePath + "/index.html";

    File file = new File(filepath);
    
    if(file.exists() && file.isFile()) {
      return serveFile(file);
    }

    return null;
  }

  private HttpResponse serveFile(File file) {
    byte[] fileContent = null;

    try (FileInputStream flujo = new FileInputStream(file)){
      fileContent = flujo.readAllBytes();
    } catch (Exception e) {
      System.err.println("Error al leer el archivo");
      e.printStackTrace();
    }

    HttpResponse response = new HttpResponse();
    String fileExtension = fileService.getFileExtension(file);
    String contentType = MIME_TYPES.getInstance().getTypeMime(fileExtension);
    response.addHeader("Content-Type", contentType);
    response.addHeader("Content-Length", String.valueOf(fileContent.length));

    response.setBody(fileContent);
    System.out.println(response.generateResponseHeaders());
    return response;
  }
}

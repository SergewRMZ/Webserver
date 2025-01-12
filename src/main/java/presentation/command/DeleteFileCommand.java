package presentation.command;

import domain.error.CustomError;
import presentation.http.HttpResponse;
import presentation.services.FileService;

public class DeleteFileCommand implements Command {
  private String headers;
  private String basePath;
  private byte[] body;
  private FileService fileService;

  public DeleteFileCommand(String headers, byte[] body) {
    this.headers = headers;
    this.body = body;
    this.basePath = "public";
    this.fileService = new FileService();
  }

  @Override
  public HttpResponse execute() {
    if(headers.contains("/api/files")) return handleDeleteFile();
    return null;
  }

  private HttpResponse handleDeleteFile() {
    try {
      String ruta = headers.split(" ")[1];
      String resource = basePath + ruta.replaceFirst("/api/files", "");
      System.out.println("Recurso por eliminar: " + resource);
      return fileService.deleteFile(resource);
    } catch (Exception e) {
      return CustomError.internalServer("Internal Server Error");
    }
  }
} 
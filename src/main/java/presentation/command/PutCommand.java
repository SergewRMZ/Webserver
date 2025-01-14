package presentation.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import domain.error.CustomError;
import presentation.http.HttpResponse;
import presentation.services.FileService;

public class PutCommand implements Command {
  private String headers;
  private String basePath;
  private byte[] body;
  private FileService fileService;

  public PutCommand (String headers, byte[] body) {
    this.headers = headers;
    this.body = body;
    this.basePath = "public";
    this.fileService = new FileService();
  }

  @Override
  public HttpResponse execute() {
    String ruta = headers.split(" ")[1];
    if(ruta.startsWith("/api/files")) {
      String resource = basePath + ruta.replace("/api/files", "");
      String bodyString = new String(body);
      JsonObject json = JsonParser.parseString(bodyString).getAsJsonObject();
      String newFileName = json.get("filename").getAsString();

      return fileService.renameFile(resource, newFileName);
    }

    return CustomError.notFound();
  }
}

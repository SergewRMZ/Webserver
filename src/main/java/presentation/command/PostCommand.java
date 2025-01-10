package presentation.command;
import presentation.http.HttpResponse;
import presentation.http.HttpUtils;
public class PostCommand implements Command {
  private String headers;
  private String basePath;
  private byte[] body;
  
  public PostCommand(String headers, byte[] body) {
    this.headers = headers;
    this.body = body;
    this.basePath = "public";
  }

  @Override
  public HttpResponse execute() {
    if(headers.contains("/api/upload/static")) return handleApiRequest();
    return null;
  }

  private HttpResponse handleApiRequest() {
    HttpResponse response = new HttpResponse();
    try {
      String contentType = HttpUtils.getContentType(headers);
      String boundary = HttpUtils.getBoundary(contentType);
      String ruta = headers.split(" ")[1];
      System.out.println("Ruta: " + ruta);
      if(boundary != null) {
        System.out.println(boundary);
      }

      String[] parts = new String(body).split("--" + boundary);
      for(String part: parts) {
        if(part.contains("Content-Disposition")) {
          String filename = part.split(";")[2].split("filename=")[1];
          byte[] fileData = extractFileData(part);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
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

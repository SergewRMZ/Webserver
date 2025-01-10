package presentation.http;

public class HttpUtils {
  public static String getContentType(String headers) {
    for(String line: headers.split("\r\n")) {
      if(line.startsWith("Content-Type:")) {
        return line.split(":")[1].trim();
      }
    }

    return null;
  }

  public static String getBoundary(String contentType) {
    String[] parts = contentType.split(";");
    if(parts.length == 2) {
      return parts[1].trim().split("boundary=")[1];
    }

    return null;
  }

  public static void multipartFormData(byte[] body, String boundary) {
    String data = new String(body);
    String[] parts = data.split(boundary);
    for(String part: parts) {
      System.out.println("Parte: " + part);
    }
  }
}

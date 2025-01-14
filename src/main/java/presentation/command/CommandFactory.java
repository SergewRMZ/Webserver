package presentation.command;

import presentation.http.HttpResponse;

public class CommandFactory {
  public static HttpResponse createCommand(String headers, byte[] body) {
    Command command;
    // System.out.println(headers);
    // System.out.println(body);

    String[] lines = headers.split("\r\n");
    String[] requestLine = lines[0].split(" ");
    String method = requestLine[0];
    String resource = requestLine[1];
    // System.out.println("Method: " + method);
    // System.out.println("Resource: " + resource);
    switch (method) {
      case "GET" -> command = new GetFileCommand(resource);
      case "POST" -> command = new PostCommand(headers, body);
      case "PUT" -> command = new PutCommand(headers, body);
      case "DELETE" -> command = new DeleteFileCommand(headers, body);
      default -> throw new AssertionError();
    }

    return command.execute();
  }
}

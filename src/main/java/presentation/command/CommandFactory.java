package presentation.command;

import presentation.http.HttpResponse;

public class CommandFactory {
  public static HttpResponse createCommand(String headers, byte[] body) {
    Command command;

    String[] lines = headers.split("\r\n");
    String[] requestLine = lines[0].split(" ");
    String method = requestLine[0];
    String resource = requestLine[1];

    switch (method) {
      case "GET" -> command = new GetFileCommand(resource);
      case "POST" -> command = new PostCommand(headers, body);
      default -> throw new AssertionError();
    }

    return command.execute();
  }
}

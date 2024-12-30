package presentation.command;

import presentation.http.HttpResponse;

public class CommandFactory {
  public static HttpResponse createCommand(String method, String resource) {
    Command command;

    switch (method) {
      case "GET" -> command = new GetFileCommand(resource);
      default -> throw new AssertionError();
    }

    return command.execute();
  }
}

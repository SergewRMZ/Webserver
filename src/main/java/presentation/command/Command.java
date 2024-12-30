package presentation.command;
import presentation.http.HttpResponse;

public interface Command {
  HttpResponse execute();
}

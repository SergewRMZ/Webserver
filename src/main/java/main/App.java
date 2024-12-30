package main;

import presentation.Server;

public class App {
  public static void main(String args[]) {
    Server server = Server.getInstance();
    server.init();
    server.listen();
  }
}

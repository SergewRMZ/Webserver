package presentation;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import presentation.command.CommandFactory;
import presentation.http.HttpResponse;

public class HandleClient implements Runnable {
  private SocketChannel client;
  public HandleClient(SocketChannel client) {
    this.client = client;
  }

  @Override
  public void run() {
    try {
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      ByteArrayOutputStream data = new ByteArrayOutputStream();
      
      int bytesRead = 0;
      
      while (client.isOpen() && (bytesRead = client.read(buffer)) > 0) {
        buffer.flip();
        
        data.write(buffer.array(), 0, buffer.limit());
        buffer.clear();
      }

      if (bytesRead == -1) {
        System.out.println("El cliente cerró la conexión.");
        client.close();
        return;
      }

      byte[] requestBytes = data.toByteArray();
      String request = new String(requestBytes);
      System.out.println("Longitud de la request: " + request.length());
      String[] parts = request.split("\r\n\r\n", 2);
      String headers = parts[0];
      byte[] body = parts.length > 1 ? parts[1].getBytes() : null;
      HttpResponse response = processRequest(headers, body);

      if(response != null) {
        sendResponse(client, response);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private HttpResponse processRequest(String headers, byte[] body) {
    return CommandFactory.createCommand(headers, body);
  }

  private void sendResponse (SocketChannel client, HttpResponse response) {
    try {
      ByteBuffer responseBuffer = ByteBuffer.wrap(response.generateResponse());
      // System.out.println(new String(responseBuffer.array()));
      while (responseBuffer.hasRemaining()) {
        client.write(responseBuffer);
      }

      System.out.println("Respuesta enviada correctamente en fragmentos");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

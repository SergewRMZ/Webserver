package presentation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import presentation.command.CommandFactory;
import presentation.http.HttpResponse;

public class Server {
  private static Server instance;
  private final int PORT = 8080;
  private ServerSocketChannel serverSocket;
  private Selector selector;

  private Server() {}

  public static Server getInstance() {
    if(instance == null) {
      instance = new Server();
    }

    return instance;
  }

  public void init() {
    try {
      serverSocket = ServerSocketChannel.open();
      serverSocket.configureBlocking(false);
      serverSocket.bind(new InetSocketAddress(PORT));

      selector = Selector.open();
      serverSocket.register(selector, SelectionKey.OP_ACCEPT);
      System.out.println("Servidor iniciado en el puerto: " + PORT);
    } catch (Exception e) {
      System.err.println("Error al abrir el socket");
      e.printStackTrace();
    }
  }

  public void listen() {
    try {
      while(true) {
        selector.select();
        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
  
        while(keys.hasNext()) {
          SelectionKey key = (SelectionKey) keys.next();
          keys.remove();
  
          if(key.isAcceptable()) {
            handleAccept();
            continue;
          }
  
          else if(key.isReadable()) {
            handleRead(key);
            continue;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleAccept() throws IOException {
    SocketChannel client = serverSocket.accept();
    System.out.println("Client conectado desde " + client.socket().getInetAddress() + ":" + client.socket().getPort());
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
  }

  private void handleRead(SelectionKey key) throws IOException {
    SocketChannel client = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    buffer.clear();
    
    int bytesRead = client.read(buffer);
    if (bytesRead == -1) {
      System.out.println("El cliente cerró la conexión.");
      client.close();
      return;
    }

    if (bytesRead > 0) {
      buffer.flip(); 
      String request = new String(buffer.array(), 0, buffer.limit());
      System.out.println("Solicitud recibida:\n" + request);
      HttpResponse response = processRequest(request);

      if(response != null) {
        sendResponse(client, response);
      }
    }

  }

  private void sendResponse (SocketChannel client, HttpResponse response) {
    try {
      ByteBuffer responseBuffer = ByteBuffer.wrap(response.generateResponse());
      while (responseBuffer.hasRemaining()) {
        client.write(responseBuffer);
      }

      System.out.println("Respuesta enviada correctamente en fragmentos");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private HttpResponse processRequest(String request) {
    String[] lines = request.split("\r\n");

    String[] requestLine = lines[0].split(" ");
    String method = requestLine[0];
    String resource = requestLine[1];
    return CommandFactory.createCommand(method, resource);
  }
}

package presentation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import presentation.command.CommandFactory;
import presentation.http.HttpResponse;

public class Server {
  private static Server instance;
  private final int PORT = 8080;
  private ServerSocketChannel serverSocket;
  private Selector selector;
  private ExecutorService threadPool;

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

      threadPool = Executors.newFixedThreadPool(8);
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
            // threadPool.execute(() -> {
            //   try {
            //     handleRead(key);
            //   } catch (IOException e) {
            //     e.printStackTrace();
            //   }
            // });
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
    ByteArrayOutputStream data = new ByteArrayOutputStream();
    
    int bytesRead;
    
    while ((bytesRead = client.read(buffer)) > 0) {
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

  private HttpResponse processRequest(String headers, byte[] body) {
    return CommandFactory.createCommand(headers, body);
  }
}

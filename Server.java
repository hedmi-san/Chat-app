package server;

import java.io.IOException;
import java.net.*;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket server) {
        this.serverSocket = server;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new client connected");
                ClientHandler client = new ClientHandler(clientSocket);
                Thread thread = new Thread(client);
                thread.start();
            }
        } catch (Exception e) {
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        ServerSocket _server = new ServerSocket(9999);
        Server server = new Server(_server);
        server.startServer();
    }

}

/*
source : https://www.youtube.com/watch?v=gLfuZrrfKes&t=293s
15:39
*/
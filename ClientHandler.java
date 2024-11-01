package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    /* 
    an array list of every clienthandler object that we create 
    every instance of the class stored in this list 
    the main purpose of this list is to keep track of all the client 
    so whenever a client send a message we can loop through our list of client 
    and either broadcast that client message to all or multiple client
    or the server can reply to that client 
     */
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList();

    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;

    public ClientHandler(Socket client) throws IOException {
        try {
            this.clientSocket = client;
            /*
            basically a socket represent a connection between a server and a client 
            and each connection has an output stream that can read data 
            and input stream that can send data 
            in each stream there are two types : 
            byte stream and character stream 
            the buffer make the communication more efficient 
            
             */
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientName = bufferedReader.readLine();
            clientHandlers.add(this);
            broadCastMessage("SERVER : " + clientName + " has entered the chat!");
        } catch (Exception e) {
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (clientSocket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadCastMessage(messageFromClient);
            } catch (Exception e) {
                try {
                    closeEverything(clientSocket, bufferedReader, bufferedWriter);
                } catch (IOException ex) {
                }
                break;
            }
        }

    }

    public void broadCastMessage(String messageToSend) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientName.equals(clientName)) {
                    clientHandler.bufferedWriter.write(messageToSend + "\n");
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {
                closeEverything(clientSocket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() throws IOException {
        clientHandlers.remove(this);
        broadCastMessage("SERVER : " + clientName + " left the chat!");
    }

    public void closeEverything(Socket s, BufferedReader br, BufferedWriter bw) throws IOException {
        removeClientHandler();
        try {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
            if (s != null) {
                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

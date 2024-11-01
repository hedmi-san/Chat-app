package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = userName;
        } catch (Exception e) {
        }
    }

    public void sendMessage(String message) {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.write(userName);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                Scanner scan = new Scanner(System.in);
                while (socket.isConnected()) {
                    String messageToSend = scan.nextLine();
                    bufferedWriter.write(userName + ": " + messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (Exception e) {
            try {
                closeEverything(socket, bufferedReader, bufferedWriter);
            } catch (IOException ex) {
            }
        }
    }

    /*here we are going to use a thread so that the process is'nt block waiting for messages */

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChat;
                while (socket.isConnected()) {                    
                    try {
                        msgFromChat = bufferedReader.readLine();
                        System.out.println(msgFromChat);
                    } catch (Exception e) {
                        try {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                        } catch (IOException ex) {
                           
                        }
                    }
                }
            }
        }).start(); //we called start because it an anonymous thread object
    }

    public void closeEverything(Socket s, BufferedReader br, BufferedWriter bw) throws IOException {
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
    
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your name to connect with the group chat: ");
        String username = scan.nextLine();
        Socket socket = new Socket("localhost",9999);
        Client client = new Client(socket,username);
        client.listenForMessage();
        client.sendMessage(username);
    }

}

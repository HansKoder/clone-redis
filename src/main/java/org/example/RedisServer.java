package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RedisServer {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(6379);

        Socket clientSocket = serverSocket.accept();

        DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());

        String command = dataIn.readUTF();
        if (command.toUpperCase().equals("PING")) {
            dataOut.writeUTF("PONG");
        } else {
            dataOut.writeUTF("(error) ERR unknown command " + command + ", with args beginning with:");
        }

        dataIn.close();
        dataOut.close();
        serverSocket.close();
    }
}
package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class RedisServer {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(6379);

        System.out.println("Ready for accepting connections!");

        Socket clientSocket = serverSocket.accept();

        DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());

        while (true) {
            String command = dataIn.readUTF().toUpperCase();
            System.out.println("command received " + command);
            if (command.equals("PING")) {
                dataOut.writeUTF("PONG");
            } else if(command.toUpperCase().equals("QUIT")) {
               break;
            } else {
                dataOut.writeUTF("(error) ERR unknown command " + command + ", with args beginning with:");
            }
        }

        dataIn.close();
        dataOut.close();
        serverSocket.close();
    }
}
package org.example;

import org.example.command.Handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int SERVER_PORT = 6379;

    private ServerSocket serverSocket;

    private Handler handler;

    public void connect () {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Ready for accepting connections!");

            handler = new Handler();

            while (true) connecting();

        }catch (IOException io) {
            System.out.println("(error) " + io.getMessage());
            io.printStackTrace();
        }
    }

    public void connecting () throws IOException {
        Socket clientSocket = serverSocket.accept();

        String host = clientSocket.getLocalAddress().toString();
        int port = clientSocket.getPort();

        String detailClient =  host.substring(1,host.length()) + ":" + port;

        new Thread(() -> {
            try {
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());

                System.out.println(detailClient + "> connected");

                while (true) {
                    String command = dataIn.readUTF();
                    System.out.println(detailClient + "> " + command);

                    dataOut.writeUTF(handler.process(command));

                    if (handler.isStop()) break;
                }

                System.out.println(detailClient + "> disconnected" );

                dataIn.close();
                dataOut.close();
                clientSocket.close();
            } catch (IOException io) {
                io.printStackTrace();
                System.out.println(io.getMessage());
            }

        }).start();
    }

    public static void main(String[] args)  {
        Server server = new Server();
        server.connect();
    }
}
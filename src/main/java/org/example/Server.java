package org.example;

import org.example.command.Handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Server {

    public static final int SERVER_PORT = 6379;

    private static final Logger logger = LogManager.getLogger(Server.class);

    private ServerSocket serverSocket;

    private Handler handler;

    public Server() {
        setUp();
    }

    public void setUp () {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            logger.info("Ready for accepting connections!");

            handler = new Handler();
        }catch (IOException io) {
            logger.info(String.format("Cannot get up server tcp %s", io.getMessage()));
        }

    }

    public void connect () {
        try {
            while (true) connecting();
        } catch (IOException e) {
            logger.info(String.format("Error when try to keep communication with a client %s", e.getMessage()));
        }

    }

    private String getHostPortClient (Socket clientSocket) {
        return String.format("%s:%d", clientSocket.getLocalAddress().toString(), clientSocket.getPort());
    }


    public void connecting () throws IOException {
        Socket clientSocket = serverSocket.accept();
        String detailClient =  getHostPortClient(clientSocket);

        new Thread(() -> {
            try {
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());

                logger.info(String.format("%s > connected", detailClient));

                while (true) {
                    String command = dataIn.readUTF();
                    logger.info(String.format("%s > %s", detailClient, command));

                    dataOut.writeUTF(handler.process(command));

                    if (handler.isStop()) break;
                }

                logger.info(String.format("%s > disconnected", detailClient));

                dataIn.close();
                dataOut.close();
                clientSocket.close();
            } catch (IOException io) {
                logger.error(String.format("Error connecting client, %s", io.getMessage()));
            }

        }).start();
    }

    public static void main(String[] args)  {
        Server server = new Server();
        server.connect();
    }
}
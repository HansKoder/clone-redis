package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Client {

    public static final int PORT_SERVER = 6379;
    public static final int TIMEOUT = 1000;
    public static final String HOST_SERVER = "127.0.0.1";

    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private Scanner sc = new Scanner(System.in);

    private static final Logger logger = LogManager.getLogger(Client.class);

    public void connect () {
        socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(HOST_SERVER, PORT_SERVER), TIMEOUT);
        } catch (Exception ex) {
            logger.error(String.format("Cannot connect to server redis, detail error %s", ex.getMessage()));
            System.exit(-1);
        }
    }

    private String getInfoServer() {
        return String.format("%s:%d> ", socket.getInetAddress().getHostAddress(), socket.getPort());
    }

    private void sendMessage (String command) throws IOException {
        dataOut.writeUTF(command);
        logger.info(getResponse());
    }

    private String getResponse() throws IOException {
        return dataIn.readUTF();
    }

    public boolean shouldStop (String command) {
        return command.equals("QUIT") || command.equals("EXIT");
    }

    public void running () throws IOException {
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());

        try {
            while (socket.isConnected()) {
                logger.info(getInfoServer());
                String cmd = sc.nextLine();
                logger.info(String.format("cmd %s", cmd));

                if (shouldStop(cmd)) break;

                sendMessage(cmd);
            }
        } catch (IOException io) {
            logger.error(String.format("[Err] running %s", io.getMessage()));
        }
        finally {
            dataIn.close();
            dataOut.close();
            socket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.connect();
        client.running();
    }

}

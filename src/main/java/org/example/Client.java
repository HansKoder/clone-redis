package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;

public class Client {

    private final int PORT_SERVER = 6379;
    private final int TIMEOUT = 1000;
    private final String HOST_SERVER = "127.0.0.1";

    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private Scanner sc = new Scanner(System.in);

    public void connect () {
        socket = new Socket();

        try {
            socket.connect(new InetSocketAddress(HOST_SERVER, PORT_SERVER), TIMEOUT);
        } catch (Exception ex) {
            System.out.println("Cannot connect to server redis " + ex.getMessage());
            System.exit(-1);
        }
    }

    private String getInfoServer() {
        int port = socket.getPort();
        String host = socket.getInetAddress().getHostAddress();

        return host + ":" + port + "> ";
    }

    private String sendMessage () throws IOException {
        System.out.print(getInfoServer());
        String command = sc.nextLine();
        dataOut.writeUTF(command);
        System.out.println(getResponse());

        return command;
    }

    private String getResponse() throws IOException {
        return dataIn.readUTF();
    }

    public void running ()  {
        try {
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());

            while (true) {
                if (sendMessage().toUpperCase().equals("QUIT")) break;
            }

            dataIn.close();
            dataOut.close();
            socket.close();

        } catch (IOException io) {
            System.out.println("[Err] Running " + io.getMessage());
            io.printStackTrace();
        }
    }

    private void closeResources () throws IOException {
        dataIn.close();
        dataOut.close();
        socket.close();
    }

    public static void main(String[] args)  {
        Client client = new Client();
        client.connect();
        client.running();
    }

}

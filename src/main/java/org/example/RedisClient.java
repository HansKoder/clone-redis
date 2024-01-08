package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;

public class RedisClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();

        try {
            socket.connect(new InetSocketAddress("127.0.0.1", 6379), 1000);
        } catch (Exception ex) {
            System.out.println("Cannot connect to server redis " + ex.getMessage());
            System.exit(-1);
        }

        int port = socket.getPort();
        String host = socket.getLocalAddress().getHostAddress();

        DataInputStream dataIn = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());

        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.print(host + ":" + port + "> ");

                String command = sc.nextLine();
                dataOut.writeUTF(command);

                System.out.println(dataIn.readUTF());

                if (command.toUpperCase().equals("QUIT")) break;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                break;
            }

        }

        dataIn.close();
        dataOut.close();
        socket.close();
    }

}

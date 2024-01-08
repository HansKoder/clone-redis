package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class RedisClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1", 6379), 1000);

        int port = socket.getPort();
        String host = socket.getLocalAddress().getHostAddress();

        System.out.print(host + ":" + port + "> ");

        DataInputStream dataIn = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());

        Scanner sc = new Scanner(System.in);

        String command = sc.nextLine();
        dataOut.writeUTF(command);
        System.out.print(dataIn.readUTF());

        dataIn.close();
        dataOut.close();
        socket.close();
    }

}

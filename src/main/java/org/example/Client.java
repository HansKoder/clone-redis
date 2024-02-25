package org.example;

import org.example.server.ClientNonBlocking;
import org.example.server.SocketClient;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        ClientNonBlocking client = new ClientNonBlocking(new SocketClient(), new Scanner(System.in));
        client.start();
    }

}

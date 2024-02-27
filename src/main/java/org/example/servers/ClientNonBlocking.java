package org.example.servers;

import org.example.exceptions.CannotConnectSocketChannel;
import org.example.exceptions.CannotRunSocketChannel;

public class ClientNonBlocking {

    public static void main(String[] args) throws CannotConnectSocketChannel, CannotRunSocketChannel {
        ClientNIO clientNIO = new ClientNIO();

        clientNIO.connect();
        clientNIO.running();
    }

}

package org.example.servers;

import org.example.exceptions.CannotConnectSocketChannel;
import org.example.exceptions.CannotRunSocketChannel;

public class ServerNonBlocking {

    public static void main(String[] args) throws CannotConnectSocketChannel, CannotRunSocketChannel {
        ServerNIO serverNIO = new ServerNIO();

        serverNIO.connect();
        serverNIO.running();
    }

}

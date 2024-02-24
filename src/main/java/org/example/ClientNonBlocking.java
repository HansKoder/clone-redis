package org.example;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.example.ServerNonBlocking.SERVER_PORT;

public class ClientNonBlocking {

    public static final String SERVER_HOST = "127.0.0.1";

    private static final Logger logger = LogManager.getLogger(ClientNonBlocking.class);

    private static final Scanner sc = new Scanner(System.in);


    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(SERVER_HOST, SERVER_PORT))) {
            while (true) {
                logger.printf(Level.INFO,"%s:%d> ",SERVER_HOST, SERVER_PORT);

                String message = sc.nextLine();

                if (message.equalsIgnoreCase("QUIT") || message.equalsIgnoreCase("EXIT")) break;

                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                socketChannel.write(buffer);

                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();

                CharBuffer responseServer = StandardCharsets.UTF_8.newDecoder().decode(buffer);
                logger.info(responseServer);
            }

        } catch (IOException exception) {
            logger.printf(Level.ERROR,"cannot run client non-blocking %s", exception.getMessage());
        }
    }

}

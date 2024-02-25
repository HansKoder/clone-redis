package org.example.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientNonBlocking implements IServer{

    public static final String SERVER_HOST = "127.0.0.1";
    public static final int SERVER_PORT = 6379;

    private static final Logger logger = LogManager.getLogger(ClientNonBlocking.class);

    private final SocketWrapper<SocketChannel> socketWrapper;

    private final Scanner scanner;

    public ClientNonBlocking(SocketWrapper<SocketChannel> socketWrapper, Scanner scanner) {
        this.socketWrapper = socketWrapper;
        this.scanner = scanner;
    }

    private boolean isStop (String message) {
        return message.equalsIgnoreCase("QUIT") || message.equalsIgnoreCase("EXIT");
    }

    @Override
    public void start() {
        socketWrapper.createAndListen(SERVER_HOST, SERVER_PORT);

        while ( socketWrapper.getSocket().isOpen() ) {
            logger.printf(Level.INFO,"%s:%d> ",SERVER_HOST, SERVER_PORT);
            String message = scanner.nextLine();

            if (isStop(message)) socketWrapper.close();

            socketWrapper.write(message);
            logger.info(socketWrapper.read());
        }
    }
}

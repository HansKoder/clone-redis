package org.example.servers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Global;
import org.example.exceptions.CannotConnectSocketChannel;
import org.example.exceptions.CannotRunSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;

public class ClientNIO implements IServer {

    private static final Logger logger = LogManager.getLogger(ClientNIO.class);

    private SocketChannel socketChannel;

    private final String host;
    private final int port;

    private final Scanner scanner;

    public ClientNIO() {
        this(Global.SERVER_HOST, Global.SERVER_PORT);
    }

    public ClientNIO(String host, int port) {
        this.host = host;
        this.port = port;
        this.scanner = new Scanner(System.in);
    }

    private String getHost () {
        return String.format("%s:%d", host, port);
    }

    private boolean isStop (String message) {
        return message.equalsIgnoreCase("QUIT") || message.equalsIgnoreCase("EXIT");
    }

    @Override
    public void connect() throws CannotConnectSocketChannel {
        try {
            logger.printf(Level.INFO, "%s is running with successful", getHost());
            this.socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        } catch (IOException err) {
            String msg = String.format("cannot run client socket non-blocking %s", err.getMessage());
            throw new CannotConnectSocketChannel(msg);
        }
    }

    @Override
    public void running() throws CannotRunSocketChannel {
        if (Optional.ofNullable(socketChannel).isEmpty() ||  !socketChannel.isOpen()) {
            throw new CannotRunSocketChannel("Must be connected the socket channel before running");
        }

        while ( socketChannel.isOpen()) {
            logger.info(this::getHost);
            String message = scanner.nextLine();

            if (isStop(message)) this.shutDown();

            this.write(message);
            String response = this.read();
            logger.info(response);
        }
    }

    @Override
    public void shutDown() {
        try {
            socketChannel.close();
        } catch (IOException err) {
            logger.printf(Level.ERROR, "cannot close client socket non-blocking %s", err.getMessage());
        }
    }

    private String read() throws CannotRunSocketChannel {
        try {
            ByteBuffer bufferResponse = ByteBuffer.allocate(1024);
            socketChannel.read(bufferResponse);
            bufferResponse.flip();
            return StandardCharsets.UTF_8.newDecoder().decode(bufferResponse).toString();
        } catch (IOException err) {
            String msg = String.format("cannot read client socket non-blocking %s", err.getMessage());
            throw new CannotRunSocketChannel(msg);
        }
    }

    private void write(String message) throws CannotRunSocketChannel {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(buffer);
            buffer.clear();
        } catch (IOException err) {
            String msg = String.format("cannot write client socket non-blocking %s", err.getMessage());
            throw new CannotRunSocketChannel(msg);
        }
    }

}

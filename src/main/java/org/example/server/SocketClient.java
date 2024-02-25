package org.example.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SocketClient implements SocketWrapper<SocketChannel>{

    private static final Logger logger = LogManager.getLogger(SocketClient.class);

    private SocketChannel socketChannel;

    @Override
    public void createAndListen(String host, int port) {
        try {
            logger.info("create and listen..");
            this.socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        } catch (IOException err) {
            logger.printf(Level.ERROR,"cannot run client socket non-blocking %s", err.getMessage());
        }
    }

    @Override
    public String read() {
        try {
            ByteBuffer bufferResponse = ByteBuffer.allocate(1024);
            socketChannel.read(bufferResponse);
            bufferResponse.flip();

            CharBuffer responseServer = StandardCharsets.UTF_8.newDecoder().decode(bufferResponse);

            return responseServer.toString();

        } catch (IOException err) {
            logger.printf(Level.ERROR,"cannot write client socket non-blocking %s", err.getMessage());
            return "";
        }
    }

    @Override
    public void write(String message) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(buffer);
            buffer.clear();
        } catch (IOException err) {
            logger.printf(Level.ERROR,"cannot write client socket non-blocking %s", err.getMessage());
        }
    }

    @Override
    public SocketChannel getSocket() {
        return this.socketChannel;
    }

    @Override
    public void close() {
        try {
            socketChannel.close();
        } catch (IOException err) {
            logger.printf(Level.ERROR,"cannot close client socket non-blocking %s", err.getMessage());
        }
    }
}

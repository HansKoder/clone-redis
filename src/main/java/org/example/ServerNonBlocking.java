package org.example;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.command.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class ServerNonBlocking {

    public static final int SERVER_PORT = 6379;

    private static final Logger logger = LogManager.getLogger(ServerNonBlocking.class);

    public static void main(String[] args) {
        Handler handler = new Handler();

        try (Selector selector = Selector.open(); ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.printf(Level.INFO,"Server started on port %d", SERVER_PORT);

            int counter = 0;
            while (counter < Integer.MAX_VALUE) {
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel clientChannel = channel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        logger.printf(Level.INFO, "New connection accepted: %s ", clientChannel.getRemoteAddress());
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int bytesRead = clientChannel.read(buffer);
                        if (bytesRead == -1) {
                            logger.printf(Level.INFO, "Connection closed by client: %s", clientChannel.getRemoteAddress());
                            clientChannel.close();
                            key.cancel();
                        } else if (bytesRead > 0) {

                            buffer.flip();
                            byte[] data = new byte[buffer.remaining()];
                            buffer.get(data);
                            String message = new String(data, StandardCharsets.UTF_8);

                            logger.printf(Level.INFO,"Received message from %s", clientChannel.getRemoteAddress() + ": " + message);

                            // Echo the message back to the client
                            String response = handler.process(message);
                            logger.printf(Level.INFO,"response server %s", response);

                            ByteBuffer bufferResponse = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
                            clientChannel.write(bufferResponse);
                        }
                    }

                    keyIterator.remove();
                }

                counter++;
            }
        } catch (IOException exception) {
            logger.printf(Level.ERROR,"cannot run server non-blocking %s", exception.getMessage());
        }

    }

}

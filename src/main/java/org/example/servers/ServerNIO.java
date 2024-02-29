package org.example.servers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.command.Handler;
import org.example.config.Global;
import org.example.exceptions.CannotConnectSocketChannel;
import org.example.exceptions.CannotRunSocketChannel;

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

public class ServerNIO implements IServer {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    private final String host;
    private final int port;

    private static final Logger logger = LogManager.getLogger(ServerNIO.class);

    private final Handler handler;

    public ServerNIO() {
        this(Global.SERVER_HOST, Global.SERVER_PORT);
    }

    public ServerNIO(String host, int port) {
        this.host = host;
        this.port = port;
        this.handler = new Handler();
    }

    @Override
    public void connect() throws CannotConnectSocketChannel {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.printf(Level.INFO, "%s:%d is running", host, port);
        } catch (IOException err) {
            String msg = String.format("cannot run server socket non-blocking %s", err.getMessage());
            throw new CannotConnectSocketChannel(msg);
        }
    }

    private void addClientChannel(SelectionKey key) throws IOException {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = channel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        logger.printf(Level.INFO, "New connection accepted: %s ", clientChannel.getRemoteAddress());
    }

    private void read(SelectionKey key, SocketChannel clientChannel) throws IOException {
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
            logger.printf(Level.INFO, "Received message from %s", clientChannel.getRemoteAddress() + ": " + message);

            this.write(clientChannel, message);
        }
    }

    private void write(SocketChannel clientChannel, String message) throws IOException {
        String response = handler.process(message);
        logger.printf(Level.INFO, "response server %s", response);

        ByteBuffer bufferResponse = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
        clientChannel.write(bufferResponse);
    }

    @Override
    public void running() throws CannotRunSocketChannel {
        try {
            while (serverSocketChannel.isOpen()) {
                selector.select();

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        addClientChannel(key);
                    } else if (key.isReadable()) {
                        read(key, (SocketChannel) key.channel());
                    }

                    keyIterator.remove();
                }
            }
        } catch (IOException err) {
            String msg = String.format("cannot be run server socket non-blocking %s", err.getMessage());
            throw new CannotRunSocketChannel(msg);
        }
    }

    @Override
    public void shutDown() {
        try {
            selector.close();
            serverSocketChannel.close();
        } catch (IOException err) {
            logger.printf(Level.ERROR, "cannot close server socket non-blocking %s", err.getMessage());
        }
    }
}

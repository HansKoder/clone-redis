package org.example.server;

public interface SocketWrapper<T> {

    void createAndListen (String host, int port);

    String read ();

    void write (String payload);

    T getSocket ();

    void close ();
}

package org.example.servers;

import org.example.exceptions.CannotConnectSocketChannel;
import org.example.exceptions.CannotRunSocketChannel;

public interface IServer {
    void connect () throws CannotConnectSocketChannel;
    void running () throws CannotRunSocketChannel;
    void shutDown ();
}

package servers;

import org.example.exceptions.CannotConnectSocketChannel;
import org.example.exceptions.CannotRunSocketChannel;
import org.example.servers.ServerNIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.channels.*;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

class ServerNIOTest {


    @Test
    @DisplayName("Should get an exception called CannotConnectSocketChannel since the static method get an IOException")
    void shouldGetConnectionError () throws IOException {
        ServerNIO server = new ServerNIO();

        ServerSocketChannel serverSocketChannelMock = mock(ServerSocketChannel.class);
        Selector selectorMock = mock(Selector.class);

        try (MockedStatic<Selector> selectorMockedStatic = mockStatic(Selector.class);
             MockedStatic<ServerSocketChannel> serverMockedStatic = mockStatic(ServerSocketChannel.class)) {

            selectorMockedStatic.when(Selector::open).thenReturn(selectorMock);
            serverMockedStatic.when(ServerSocketChannel::open).thenThrow(new IOException("error"));

            Exception exception = assertThrows(CannotConnectSocketChannel.class, server::connect);
            assertNotNull(exception);
            assertEquals("cannot run server socket non-blocking error", exception.getMessage());

            Mockito.verify(serverSocketChannelMock, times(0)).bind(any());
        }
    }

    @Test
    @DisplayName("Should add a new socket channel with successful")
    void shouldAddNewChannel () throws IOException, CannotConnectSocketChannel, CannotRunSocketChannel {
        ServerNIO server = new ServerNIO();

        ServerSocketChannel serverSocketChannelMock = mock(ServerSocketChannel.class);
        SocketChannel socketChannelMock = mock(SocketChannel.class);
        Selector selectorMock = mock(Selector.class);

        SelectionKey selectionKeyMock = mock(SelectionKey.class);

        try (MockedStatic<Selector> selectorMockedStatic = mockStatic(Selector.class);
             MockedStatic<ServerSocketChannel> serverMockedStatic = mockStatic(ServerSocketChannel.class)) {

            selectorMockedStatic.when(Selector::open).thenReturn(selectorMock);
            serverMockedStatic.when(ServerSocketChannel::open).thenReturn(serverSocketChannelMock);

            // Add true after false for avoiding loop infinite
            Mockito.when(serverSocketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.FALSE);
            Mockito.when(selectorMock.select()).thenReturn(1);

            // Add Mock for selectedKeys
            Set<SelectionKey> list = new HashSet<>();
            list.add(selectionKeyMock);
            when(selectorMock.selectedKeys()).thenReturn(list);

            when(selectionKeyMock.isAcceptable()).thenReturn(true);
            when(selectionKeyMock.channel()).thenReturn(serverSocketChannelMock);

            when(serverSocketChannelMock.accept()).thenReturn(socketChannelMock);

            assertDoesNotThrow(server::connect);
            assertDoesNotThrow(server::running);

            Mockito.verify(selectorMock, times(1)).select();
            Mockito.verify(selectionKeyMock, times(1)).isAcceptable();
            Mockito.verify(selectionKeyMock, times(0)).isReadable();
        }
    }


    @Test
    @DisplayName("Should get an exception called CannotRunSocketChannel")
    void shouldGetExceptionCalledCannotRunServer () throws IOException, CannotConnectSocketChannel, CannotRunSocketChannel {
        ServerNIO server = new ServerNIO();

        ServerSocketChannel serverSocketChannelMock = mock(ServerSocketChannel.class);
        Selector selectorMock = mock(Selector.class);

        SelectionKey selectionKeyMock = mock(SelectionKey.class);

        try (MockedStatic<Selector> selectorMockedStatic = mockStatic(Selector.class);
             MockedStatic<ServerSocketChannel> serverMockedStatic = mockStatic(ServerSocketChannel.class)) {

            selectorMockedStatic.when(Selector::open).thenReturn(selectorMock);
            serverMockedStatic.when(ServerSocketChannel::open).thenReturn(serverSocketChannelMock);

            // Add true after false for avoiding loop infinite
            Mockito.when(serverSocketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.FALSE);
            Mockito.when(selectorMock.select()).thenReturn(1);

            // Add Mock for selectedKeys
            Set<SelectionKey> list = new HashSet<>();
            list.add(selectionKeyMock);
            when(selectorMock.selectedKeys()).thenReturn(list);

            when(selectionKeyMock.isAcceptable()).thenReturn(true);
            when(selectionKeyMock.channel()).thenReturn(serverSocketChannelMock);

            when(serverSocketChannelMock.accept()).thenThrow(new IOException("Error IO"));

            assertDoesNotThrow(server::connect);

            Exception exception = assertThrows(CannotRunSocketChannel.class, server::running);
            assertNotNull(exception);
            assertEquals("cannot be run server socket non-blocking Error IO", exception.getMessage());

            Mockito.verify(selectorMock, times(1)).select();
            Mockito.verify(selectionKeyMock, times(1)).isAcceptable();
            Mockito.verify(selectionKeyMock, times(0)).isReadable();
        }
    }

}

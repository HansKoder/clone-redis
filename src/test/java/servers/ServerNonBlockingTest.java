package servers;

import org.example.exceptions.CannotConnectSocketChannel;
import org.example.exceptions.CannotRunSocketChannel;
import org.example.servers.ServerNonBlocking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServerNonBlockingTest {

    @Test
    @DisplayName("Should get an new exception called CannotConnectServerSocket since it is not working")
    void shouldGetExceptionCannotConnectServerSocket () throws IOException {
        ServerSocketChannel serverSocketChannelMock = mock(ServerSocketChannel.class);
        Selector selectorMock = mock(Selector.class);

        try (MockedStatic<Selector> selectorMockedStatic = mockStatic(Selector.class);
             MockedStatic<ServerSocketChannel> serverMockedStatic = mockStatic(ServerSocketChannel.class)) {

            selectorMockedStatic.when(Selector::open).thenReturn(selectorMock);
            serverMockedStatic.when(ServerSocketChannel::open).thenThrow(new IOException("Error I/O"));

            String[] p = {""};
            Exception exception = assertThrows(CannotConnectSocketChannel.class, () -> ServerNonBlocking.main(p));
            assertEquals("cannot run server socket non-blocking Error I/O", exception.getMessage());

            Mockito.verify(serverSocketChannelMock, times(0)).bind(any());
        }
    }


    @Test
    @DisplayName("Should get a new exception called CannotRunSocketChannel since it is not possible to write")
    void shouldGetExceptionCannotRunServerSocket () throws IOException {

        ServerSocketChannel serverSocketChannelMock = mock(ServerSocketChannel.class);
        Selector selectorMock = mock(Selector.class);
        SelectionKey selectionKeyMock = mock(SelectionKey.class);
        SocketChannel clientSocketChannel = mock(SocketChannel.class);

        try (MockedStatic<Selector> selectorMockedStatic = mockStatic(Selector.class);
             MockedStatic<ServerSocketChannel> serverMockedStatic = mockStatic(ServerSocketChannel.class)) {

            selectorMockedStatic.when(Selector::open).thenReturn(selectorMock);
            serverMockedStatic.when(ServerSocketChannel::open).thenReturn(serverSocketChannelMock);

            // Add true after false for avoiding loop infinite
            Mockito.when(serverSocketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.FALSE);

            Mockito.when(selectorMock.select()).thenReturn(1);

            when(selectionKeyMock.isAcceptable()).thenReturn(Boolean.FALSE);
            when(selectionKeyMock.isReadable()).thenReturn(Boolean.TRUE);

            // Add Mock for selectedKeys
            Set<SelectionKey> list = new HashSet<>();
            list.add(selectionKeyMock);
            when(selectorMock.selectedKeys()).thenReturn(list);

            // Client Socket Channel Mock
            when(selectionKeyMock.channel()).thenReturn(clientSocketChannel);
            when(clientSocketChannel.read(any(ByteBuffer.class))).thenReturn(10);
            when(clientSocketChannel.write(any(ByteBuffer.class))).thenThrow(new IOException("Error I/O"));

            SocketAddress socketAddressMock = mock(SocketAddress.class);
            when(socketAddressMock.toString()).thenReturn("127.0.0.1:6379");
            when(clientSocketChannel.getRemoteAddress()).thenReturn(socketAddressMock);

            // Assertions
            String[] p = {""};
            Exception exception = assertThrows(CannotRunSocketChannel.class, () -> ServerNonBlocking.main(p));

            assertEquals("cannot be run server socket non-blocking Error I/O", exception.getMessage());

            Mockito.verify(serverSocketChannelMock, times(1)).bind(any());
            Mockito.verify(selectorMock, times(1)).select();
            Mockito.verify(selectionKeyMock, times(1)).isAcceptable();
            Mockito.verify(selectionKeyMock, times(1)).isReadable();
        }
    }


    @Test
    @DisplayName("Should get a new exception called CannotRunSocketChannel since it is not possible to write")
    void shouldHandleCommand () throws IOException {

        ServerSocketChannel serverSocketChannelMock = mock(ServerSocketChannel.class);
        Selector selectorMock = mock(Selector.class);
        SelectionKey selectionKeyMock = mock(SelectionKey.class);
        SocketChannel clientSocketChannel = mock(SocketChannel.class);

        try (MockedStatic<Selector> selectorMockedStatic = mockStatic(Selector.class);
             MockedStatic<ServerSocketChannel> serverMockedStatic = mockStatic(ServerSocketChannel.class)) {

            selectorMockedStatic.when(Selector::open).thenReturn(selectorMock);
            serverMockedStatic.when(ServerSocketChannel::open).thenReturn(serverSocketChannelMock);

            // Add true after false for avoiding loop infinite
            Mockito.when(serverSocketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.FALSE);

            Mockito.when(selectorMock.select()).thenReturn(1);

            when(selectionKeyMock.isAcceptable()).thenReturn(Boolean.FALSE);
            when(selectionKeyMock.isReadable()).thenReturn(Boolean.TRUE);

            // Add Mock for selectedKeys
            Set<SelectionKey> list = new HashSet<>();
            list.add(selectionKeyMock);
            when(selectorMock.selectedKeys()).thenReturn(list);

            // Client Socket Channel Mock
            when(selectionKeyMock.channel()).thenReturn(clientSocketChannel);
            when(clientSocketChannel.read(any(ByteBuffer.class))).thenReturn(10);
            when(clientSocketChannel.write(any(ByteBuffer.class))).thenReturn(10);

            SocketAddress socketAddressMock = mock(SocketAddress.class);
            when(socketAddressMock.toString()).thenReturn("127.0.0.1:6379");
            when(clientSocketChannel.getRemoteAddress()).thenReturn(socketAddressMock);

            doNothing().when(serverSocketChannelMock).close();
            doNothing().when(selectorMock).close();

            // Assertions
            String[] p = {""};
            assertDoesNotThrow(() -> ServerNonBlocking.main(p));

            Mockito.verify(serverSocketChannelMock, times(1)).bind(any());
            Mockito.verify(selectorMock, times(1)).select();
            Mockito.verify(selectionKeyMock, times(1)).isAcceptable();
            Mockito.verify(selectionKeyMock, times(1)).isReadable();
        }
    }


}

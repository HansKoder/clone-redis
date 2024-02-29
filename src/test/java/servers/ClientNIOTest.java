package servers;

import org.example.exceptions.CannotConnectSocketChannel;
import org.example.exceptions.CannotRunSocketChannel;
import org.example.servers.ClientNIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientNIOTest {

    @Test
    @DisplayName("Should get an exception called CannotConnectSocketChannel")
    void shouldThrowErrorCannotConnection () {
        ClientNIO client = new ClientNIO();

        SocketChannel socketChannelMock = mock(SocketChannel.class);

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class) ){
            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenThrow(new IOException("Error I/O"));

            Exception exception = assertThrows(CannotConnectSocketChannel.class, client::connect);
            assertNotNull(exception);
            assertEquals("cannot run client socket non-blocking Error I/O", exception.getMessage());

            Mockito.verify(socketChannelMock, times(0)).isOpen();
        }
    }

    @Test
    @DisplayName("Should get a new exception called CannotRunSocketChannel since it is not running")
    void shouldGetAnExceptionCalledCannotRunSocketSinceSocketIsNotRunning () throws IOException {
        ClientNIO client = new ClientNIO();

        SocketChannel socketChannelMock = mock(SocketChannel.class);

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class); ){
            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenReturn(socketChannelMock);

            when(socketChannelMock.isOpen()).thenReturn(Boolean.FALSE);

            assertDoesNotThrow(client::connect);

            Exception exception = assertThrows(CannotRunSocketChannel.class, client::running);
            assertNotNull(exception);
            assertEquals("Must be connected the socket channel before running", exception.getMessage());

            Mockito.verify(socketChannelMock, times(0)).read(Mockito.any(ByteBuffer.class));
            Mockito.verify(socketChannelMock, times(0)).write(Mockito.any(ByteBuffer.class));
        }
    }


    @Test
    @DisplayName("Should get an exception called CannotRunSocketChannel since It is not possible to send bytes to server socket channel")
    void shouldGetExceptionCalledCannotRunSinceItIsNotPossibleToSendMessage () throws IOException {

        Scanner scannerMock = mock(Scanner.class);
        when(scannerMock.nextLine()).thenReturn("set name first-name");

        ClientNIO client = new ClientNIO("127.0.0.1", 8080, scannerMock);

        SocketChannel socketChannelMock = mock(SocketChannel.class);

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class); ){
            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenReturn(socketChannelMock);

            // Add False after the two first True for avoiding loop infinite
            when(socketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.FALSE);

            when(socketChannelMock.write(Mockito.any(ByteBuffer.class))).thenThrow(new IOException("Error I/O"));

            assertDoesNotThrow(client::connect);

            Exception exception = assertThrows(CannotRunSocketChannel.class, client::running);
            assertEquals("cannot write client socket non-blocking Error I/O", exception.getMessage());

            Mockito.verify(socketChannelMock, times(1)).write(Mockito.any(ByteBuffer.class));
            Mockito.verify(socketChannelMock, times(0)).read(Mockito.any(ByteBuffer.class));
        }
    }

    @Test
    @DisplayName("Should get an exception called CannotRunSocketChannel since It is not possible to read the bytes sent by server socket channel")
    void shouldGetExceptionCalledCannotRunSinceItIsNotPossibleToReadTheResponse () throws IOException {

        Scanner scannerMock = mock(Scanner.class);
        when(scannerMock.nextLine()).thenReturn("set name first-name");

        ClientNIO client = new ClientNIO("127.0.0.1", 8080, scannerMock);

        SocketChannel socketChannelMock = mock(SocketChannel.class);

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class); ){
            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenReturn(socketChannelMock);

            // Add False after the two first True for avoiding loop infinite
            when(socketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.FALSE);

            when(socketChannelMock.write(Mockito.any(ByteBuffer.class))).thenReturn(3);
            when(socketChannelMock.read(Mockito.any(ByteBuffer.class))).thenThrow(new IOException("Error I/O"));

            assertDoesNotThrow(client::connect);

            Exception exception = assertThrows(CannotRunSocketChannel.class, client::running);
            assertEquals("cannot read client socket non-blocking Error I/O", exception.getMessage());

            Mockito.verify(socketChannelMock, times(1)).write(Mockito.any(ByteBuffer.class));
            Mockito.verify(socketChannelMock, times(1)).read(Mockito.any(ByteBuffer.class));
        }
    }

    @Test
    @DisplayName("Should send message or command to server channel and get a response with the following text 'OK'")
    void shouldSendCommandAndGetResponse () throws IOException {

        Scanner scannerMock = mock(Scanner.class);
        when(scannerMock.nextLine()).thenReturn("set name first-name");

        ClientNIO client = new ClientNIO("127.0.0.1", 8080, scannerMock);

        SocketChannel socketChannelMock = mock(SocketChannel.class);

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class); ){
            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenReturn(socketChannelMock);

            // Add False after the two first True for avoiding loop infinite
            when(socketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.FALSE);

            when(socketChannelMock.write(Mockito.any(ByteBuffer.class))).thenReturn(3);
            when(socketChannelMock.read(Mockito.any(ByteBuffer.class))).thenReturn(3);

            assertDoesNotThrow(client::connect);
            assertDoesNotThrow(client::running);

            Mockito.verify(socketChannelMock, times(1)).write(Mockito.any(ByteBuffer.class));
            Mockito.verify(socketChannelMock, times(1)).read(Mockito.any(ByteBuffer.class));
        }
    }


    @Test
    @DisplayName("Should stop server closing current connection socket channel since the command write is QUITE")
    void shouldStopServerClosingCurrentConnection () throws IOException {

        Scanner scannerMock = mock(Scanner.class);
        when(scannerMock.nextLine()).thenReturn("QUIT");

        ClientNIO client = new ClientNIO("127.0.0.1", 8080, scannerMock);

        SocketChannel socketChannelMock = mock(SocketChannel.class);

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class); ){
            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenReturn(socketChannelMock);

            // Add False after the two first True for avoiding loop infinite
            when(socketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.FALSE);

            doNothing().when(socketChannelMock).close();

            assertDoesNotThrow(client::connect);
            assertDoesNotThrow(client::running);

            Mockito.verify(socketChannelMock, times(0)).write(Mockito.any(ByteBuffer.class));
            Mockito.verify(socketChannelMock, times(0)).read(Mockito.any(ByteBuffer.class));
        }
    }

    @Test
    @DisplayName("Should stop server without closing current connection socket channel since there is any IOException when was closed socket connection")
    void shouldStopServerWithoutClosingConnectionSocketSinceThereIsAnyIOException() throws IOException {

        Scanner scannerMock = mock(Scanner.class);
        when(scannerMock.nextLine()).thenReturn("QUIT");

        ClientNIO client = new ClientNIO("127.0.0.1", 8080, scannerMock);

        SocketChannel socketChannelMock = mock(SocketChannel.class);

        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class); ){
            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenReturn(socketChannelMock);

            // Add False after the two first True for avoiding loop infinite
            when(socketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.TRUE)
                    .thenReturn(Boolean.FALSE);

            doThrow(new IOException("Error I/O")).when(socketChannelMock).close();

            assertDoesNotThrow(client::connect);
            assertDoesNotThrow(client::running);

            Mockito.verify(socketChannelMock, times(0)).write(Mockito.any(ByteBuffer.class));
            Mockito.verify(socketChannelMock, times(0)).read(Mockito.any(ByteBuffer.class));
        }
    }

}

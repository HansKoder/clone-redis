package servers;

import org.example.exceptions.CannotConnectSocketChannel;
import org.example.exceptions.CannotRunSocketChannel;
import org.example.servers.ClientNonBlocking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientNonBlockingTest {

    @Test
    @DisplayName("should get exception called CannotConnectSocketChannel since the socket channel is not working")
    void shouldGetExceptionCannotConnectSocketChannelSinceChannelIsNotWorking () {
        try (MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class)) {
            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenThrow(new IOException("Error I/O"));

            String[] p = {""};
            Exception exception = assertThrows(CannotConnectSocketChannel.class, () -> ClientNonBlocking.main(p));
            assertEquals("cannot run client socket non-blocking Error I/O", exception.getMessage());
        }
    }

    @Test
    @DisplayName("should get exception called CannotRunSocketChannel since it is not possible to write bytes")
    void shouldGetExceptionCannotRuntSocketChannelSinceItIsNotPossibleToWriteBytes () throws IOException {

        SocketChannel socketChannelMock = mock(SocketChannel.class);

        try (
                MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class);
                MockedConstruction<Scanner> mockedConstruction = Mockito.mockConstruction(Scanner.class,(mock, context) -> {
                    when(mock.nextLine()).thenReturn("set name xx").thenReturn("EXIT");
                })) {

            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenReturn(socketChannelMock);

            when(socketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE);

            when(socketChannelMock.write(Mockito.any(ByteBuffer.class))).thenThrow(new IOException("Error I/O"));

            String[] p = {""};
            Exception exception = assertThrows(CannotRunSocketChannel.class, () -> ClientNonBlocking.main(p));
            assertEquals("cannot write client socket non-blocking Error I/O", exception.getMessage());

            Mockito.verify(socketChannelMock, times(1)).write(Mockito.any(ByteBuffer.class));
            Mockito.verify(socketChannelMock, times(0)).read(Mockito.any(ByteBuffer.class));
        }
    }

    @Test
    @DisplayName("should send a command and get an response OK")
    void shouldGetAsResponseOKSinceItIsPossibleToSendCMDAndGetAResponse () throws IOException {
        SocketChannel socketChannelMock = mock(SocketChannel.class);

        try (
                MockedStatic<SocketChannel> socketChannelMockedStatic = mockStatic(SocketChannel.class);
                MockedConstruction<Scanner> mockedConstruction = Mockito.mockConstruction(Scanner.class,(mock, context) -> {
                    when(mock.nextLine()).thenReturn("set name xx").thenReturn("QUIT");
                })) {

            socketChannelMockedStatic.when( ()-> SocketChannel.open(Mockito.any(SocketAddress.class)) )
                    .thenReturn(socketChannelMock);

            when(socketChannelMock.isOpen())
                    .thenReturn(Boolean.TRUE);

            when(socketChannelMock.write(Mockito.any(ByteBuffer.class))).thenReturn(3);
            when(socketChannelMock.read(Mockito.any(ByteBuffer.class))).thenReturn(3);

            String[] p = {""};
            assertDoesNotThrow(() -> ClientNonBlocking.main(p));

            Mockito.verify(socketChannelMock, times(1)).write(Mockito.any(ByteBuffer.class));
            Mockito.verify(socketChannelMock, times(1)).read(Mockito.any(ByteBuffer.class));
        }
    }

}

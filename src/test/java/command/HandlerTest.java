package command;

import org.example.command.Handler;
import org.example.command.SetCommand;
import org.example.store.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class HandlerTest {

    private Handler handler;

    @BeforeEach
    public void setUp () {
        handler = new Handler();
    }

    @Test
    public void shouldShowMsgErrorSinceReceivedCommandUnKnown () {
        String msg = "(error) ERR unknown command start, with args beginning with: param1 param2";
        assertEquals(msg, handler.process("start param1 param2"));
        assertFalse(handler.isStop());
    }

    @Test
    public void shouldBeTrueStopping () {
        assertEquals("", handler.process("QUIT"));
        assertTrue(handler.isStop());
    }

    @Test
    public void shouldShowMsgPongSinceReceivedPingWithoutParams () {
        assertEquals("PONG", handler.process("ping"));
        assertFalse(handler.isStop());
    }

    @Test
    public void shouldShowMsgHelloSinceReceivedPingWithParamHello () {
        assertEquals("hello", handler.process("ping hello"));
        assertFalse(handler.isStop());
    }

    @Test
    public void shouldSaveNewItemSuccessful () {
        Cache cache = mock(Cache.class);

        try (MockedStatic<Cache> cacheMockedStatic = mockStatic(Cache.class)) {
            cacheMockedStatic.when(Cache::getInstance).thenReturn(cache);

            doNothing().when(cache).set(anyString(), anyString());

            assertEquals("OK", handler.process("set nickname example"));

            Mockito.verify(cache, times(1)).set(anyString(), anyString());
        }
    }

    @Test
    public void shouldGetItemStored () {
        Cache cache = mock(Cache.class);

        try (MockedStatic<Cache> cacheMockedStatic = mockStatic(Cache.class)) {
            cacheMockedStatic.when(Cache::getInstance).thenReturn(cache);

            when(cache.get(anyString())).thenReturn("example-name");

            assertEquals("example-name", handler.process("get name"));

            Mockito.verify(cache, times(1)).get(anyString());
        }
    }
}

package command;

import org.example.command.Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

}

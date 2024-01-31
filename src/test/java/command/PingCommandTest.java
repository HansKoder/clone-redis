package command;

import org.example.command.PingCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PingCommandTest {

    private PingCommand command = new PingCommand();

    @Test
    void shouldShowMsgPongSinceTheCommandIsExecutedWithoutArgs() {
        assertEquals("PONG", command.execute(new String[]{"ping"}));
    }

    @Test
    void shouldShowMsgHelloSinceTheCommandIsExecutedWithTheArgHello() {
        assertEquals("Hello!", command.execute(new String[]{"ping", "Hello!"}));
    }

    @Test
    void shouldShowMsgErrorSinceTheCommandHasInvalidCantArgs() {
        String msg = "(error) ERR wrong number of arguments for 'ping' command";
        assertEquals(msg, command.execute(new String[]{"ping", "First Arg", "Second Arg", "Third Arg"}));
    }


}

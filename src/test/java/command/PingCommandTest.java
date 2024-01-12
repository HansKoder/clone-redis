package command;

import org.example.command.PingCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PingCommandTest {

    private PingCommand command = new PingCommand();

    @Test
    public void shouldShowMsgPongSinceTheCommandIsExecutedWithoutArgs () {
        assertEquals("PONG", command.execute(new String[] {"ping"}));
    }

    @Test
    public void shouldShowMsgHelloSinceTheCommandIsExecutedWithTheArgHello () {
        assertEquals("Hello!", command.execute(new String[] {"ping", "Hello!"}));
    }

    @Test
    public void shouldShowMsgErrorSinceTheCommandHasInvalidCantArgs () {
        String msg = "(error) ERR wrong number of arguments for 'ping' command";
        assertEquals(msg, command.execute(new String[] {"ping", "First Arg", "Second Arg", "Third Arg"}));
    }


}

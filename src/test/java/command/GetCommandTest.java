package command;

import org.example.command.GetCommand;
import org.example.store.Cache;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetCommandTest {

    @Test
    public void shouldErrorMsgSinceItReceivesJustOneParam () {
        GetCommand command = new GetCommand();

        String msg = "(error) ERR wrong number of arguments for 'get' command";
        assertEquals(msg, command.execute(new String[] {"get"}));
    }

    @Test
    public void shouldGetNameSuccessful () {
        GetCommand command = new GetCommand();
        Cache cache = mock(Cache.class);

        try (MockedStatic<Cache> cacheMockedStatic = mockStatic(Cache.class)) {
            cacheMockedStatic.when(Cache::getInstance).thenReturn(cache);
            when(cache.get(anyString())).thenReturn("example-name");

            assertEquals("example-name", command.execute(new String[] {"get", "name"}));

            Mockito.verify(cache, times(1)).get(anyString());
        }
    }

    @Test
    public void shouldGetNothingSinceNothingKeyIsNotFound () {
        GetCommand command = new GetCommand();
        Cache cache = mock(Cache.class);

        try (MockedStatic<Cache> cacheMockedStatic = mockStatic(Cache.class)) {
            cacheMockedStatic.when(Cache::getInstance).thenReturn(cache);
            when(cache.get(anyString())).thenReturn(null);

            assertEquals("(nil)",command.execute(new String[] {"get", "name"}));

            Mockito.verify(cache, times(1)).get(anyString());
        }
    }

}

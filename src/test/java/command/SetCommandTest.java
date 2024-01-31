package command;

import org.example.command.GetCommand;
import org.example.command.SetCommand;
import org.example.store.Cache;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class SetCommandTest {

    @Test
    void shouldGetErrorMsg () {
        SetCommand command = new SetCommand();

        String msg = "(error) ERR wrong number of arguments for 'set' command";
        assertEquals(msg, command.execute(new String[] {"get"}));
    }

    @Test
    void shouldAddNewItem () {
        SetCommand command = new SetCommand();
        Cache cache = mock(Cache.class);

        try (MockedStatic<Cache> cacheMockedStatic = mockStatic(Cache.class)) {
            cacheMockedStatic.when(Cache::getInstance).thenReturn(cache);

            doNothing().when(cache).set(anyString(), anyString());

            assertEquals("OK",command.execute(new String[] {"set", "nickname", "example"}));

            Mockito.verify(cache, times(1)).set(anyString(), anyString());
        }
    }

}

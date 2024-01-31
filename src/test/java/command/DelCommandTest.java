package command;

import org.example.command.DelCommand;
import org.example.store.Cache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DelCommandTest {

    @Test
    @DisplayName("Should Get an error message")
    void shouldGetErrorMsg () {
        DelCommand command = new DelCommand();

        String msg = "(error) ERR wrong number of arguments for 'del' command";
        assertEquals(msg, command.execute(new String[] {"del"}));
    }

    @Test
    @DisplayName("Should return zero")
    void shouldReturnZeroItemsDeletedSinceThereIsNotAnyKeyIsStored () {
        DelCommand command = new DelCommand();
        Cache cache = mock(Cache.class);

        try (MockedStatic<Cache> cacheMockedStatic = mockStatic(Cache.class)) {
            cacheMockedStatic.when(Cache::getInstance).thenReturn(cache);
            when(cache.del(anyString()))
                    .thenReturn(null)
                    .thenReturn(null)
                    .thenReturn(null);

            assertEquals("(integer) 0", command.execute(new String[] {"del", "key-1", "key-2", "key-3"}));

            Mockito.verify(cache, times(3)).del(anyString());
        }
    }

    @Test
    void shouldReturnTwoItemsDeleted () {
        DelCommand command = new DelCommand();
        Cache cache = mock(Cache.class);

        try (MockedStatic<Cache> cacheMockedStatic = mockStatic(Cache.class)) {
            cacheMockedStatic.when(Cache::getInstance).thenReturn(cache);
            when(cache.del(anyString()))
                    .thenReturn("value-1")
                    .thenReturn("value-2")
                    .thenReturn(null);

            assertEquals("(integer) 2", command.execute(new String[] {"del", "key-1", "key-2", "key-3"}));

            Mockito.verify(cache, times(3)).del(anyString());
        }
    }

    @Test
    void shouldReturnThreeItemsDeleted () {
        DelCommand command = new DelCommand();
        Cache cache = mock(Cache.class);

        try (MockedStatic<Cache> cacheMockedStatic = mockStatic(Cache.class)) {
            cacheMockedStatic.when(Cache::getInstance).thenReturn(cache);
            when(cache.del(anyString()))
                    .thenReturn("value-1")
                    .thenReturn("value-2")
                    .thenReturn("value-3");

            assertEquals("(integer) 3", command.execute(new String[] {"del", "key-1", "key-2", "key-3"}));

            Mockito.verify(cache, times(3)).del(anyString());
        }
    }

}

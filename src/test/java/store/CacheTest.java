package store;

import org.example.store.Cache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class CacheTest {

    @Test
    @DisplayName("should get the same hashcode when try to call 3 times instance")
    void shouldGetSameInstanceTreeTimes () {
        Cache cache = Cache.getInstance();

        for (int i = 0; i < 3; i++) {
            assertEquals(cache.hashCode(),Cache.getInstance().hashCode());
        }
    }

    @Test
    @DisplayName("should add update get and delete the key [name]")
    void shouldDoCrudFromKeyName () {
        Cache cache = Cache.getInstance();

        cache.set("name", "Doe");
        assertEquals("Doe", cache.get("name"));

        cache.set("name", "Doe2");
        assertEquals("Doe2", cache.get("name"));

        assertEquals("Doe2", cache.del("name"));
    }

    @Test
    @DisplayName("should get null when try to get an key is not storing in the cache")
    void shouldGetNull () {
        assertNull(Cache.getInstance().get("name"));
    }

}

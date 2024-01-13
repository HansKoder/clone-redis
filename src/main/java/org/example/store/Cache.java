package org.example.store;

import java.util.HashMap;
import java.util.Optional;

public class Cache {

    private HashMap<String, String> cache = new HashMap<>();
    private static Cache instance;

    public void set (String key, String value) {
        cache.put(key, value);
    }

    public String get (String key) {
        return Optional.ofNullable(cache.get(key)).orElse("(nil)");
    }

    public static Cache getInstance () {
        if (Cache.instance == null) {
            Cache.instance = new Cache();
        }

        return Cache.instance;
    }

}

package org.example.store;

import java.util.HashMap;
import java.util.Optional;

public class Cache {

    private HashMap<String, String> map = new HashMap<>();
    private static Cache instance;

    public void set (String key, String value) {
        map.put(key, value);
    }

    public String get (String key) {
        return map.get(key);
    }

    public static Cache getInstance () {
        if (Cache.instance == null) {
            Cache.instance = new Cache();
        }

        return Cache.instance;
    }

    public String del(String key) {
        return map.remove(key);
    }
}

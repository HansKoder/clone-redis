package org.example.command;

import org.example.store.Cache;

import java.util.Optional;

public class DelCommand implements ICommand{
    @Override
    public String execute(String[] args) {
        if (args.length == 1) return "(error) ERR wrong number of arguments for 'del' command";

        Cache cache = Cache.getInstance();
        int count = 0;
        for (int i = 1; i < args.length; i++)
            if (Optional.ofNullable(cache.del(args[i])).isPresent()) count++;

        return "(integer) " + count;
    }
}

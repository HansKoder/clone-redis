package org.example.command;

import org.example.store.Cache;

import java.util.Optional;

public class GetCommand implements ICommand{



    @Override
    public String execute(String[] args) {
        if (args.length != 2) return "(error) ERR wrong number of arguments for 'get' command";

        Cache cache = Cache.getInstance();
        return Optional.ofNullable(cache.get(args[1])).orElse("(nil)");
    }
}

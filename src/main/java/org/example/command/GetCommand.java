package org.example.command;

import org.example.store.Cache;

public class GetCommand implements ICommand{



    @Override
    public String execute(String[] args) {
        if (args.length != 2) return "(error) ERR wrong number of arguments for 'get' command";

        Cache cache = Cache.getInstance();
        return cache.get(args[1]);
    }
}

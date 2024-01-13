package org.example.command;

import org.example.store.Cache;

public class SetCommand implements ICommand {

    @Override
    public String execute(String[] args) {
        if (args.length != 3) return "(error) ERR wrong number of arguments for 'set' command";

        Cache cache = Cache.getInstance();
        cache.set(args[1], args[2]);

        return "OK";
    }
}

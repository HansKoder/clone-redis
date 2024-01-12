package org.example.command;

public class PingCommand implements ICommand {

    @Override
    public String execute(String[] args) {
        if (args.length >= 3) return "(error) ERR wrong number of arguments for 'ping' command";
        if (args.length == 1) return "PONG";

        return args[1];
    }
}

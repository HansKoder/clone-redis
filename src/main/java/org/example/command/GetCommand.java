package org.example.command;

public class GetCommand implements ICommand{

    @Override
    public String execute(String[] args) {
        if (args.length != 2) return "(error) ERR wrong number of arguments for 'get' command";

        return "In this moment is a mock of get!!!!";
    }
}

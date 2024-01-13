package org.example.command;

import java.util.HashMap;
import java.util.Optional;

public class Handler {

    private HashMap<String, ICommand> commands = new HashMap<>();
    private boolean stop = false;

    public Handler() {
        commands.put("ping", new PingCommand());
        commands.put("get", new GetCommand());
        commands.put("set", new SetCommand());
    }

    public String process (String params) {
            String[] args = params.split(" ");

            String firstCommand = args[0].toLowerCase();
            Optional<ICommand> command = Optional.ofNullable(commands.get(firstCommand));

            if (firstCommand.equals("quit")) {
                stop = true;
                return "";
            }

            if (command.isPresent()) return command.get().execute(args);

           return buildMsgError(args);
    }

    private String buildMsgError (String[] args) {
        StringBuilder msg = new StringBuilder();

        msg.append("(error) ERR unknown command ");
        msg.append(args[0]);
        msg.append(", with args beginning with:");

        for (int i = 1; i < args.length; i++) {
            msg.append(" " + args[i]);
        }

        return msg.toString();
    }

    public boolean isStop() {
        return stop;
    }
}

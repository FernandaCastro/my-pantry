package com.fcastro.accountservice.commandSpeech;

import com.fcastro.accountservice.exception.InvalidCommandException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandHandlerRegistry {

    private final Map<String, CommandHandler> handlers = new HashMap<>();
    private static final Map<String, String> commandMapping = new HashMap<>();

    static {
        commandMapping.put("consume", "consume");
        commandMapping.put("consumir", "consume");
    }

    public CommandHandlerRegistry(List<CommandHandler> handlerList) {
        for (CommandHandler handler : handlerList) {
            handlers.put(handler.getCommandName(), handler);
        }
    }

    public CommandHandler getHandler(String commandName) {
        String commandKey = commandMapping.get(commandName);
        if (commandKey == null) {
            throw new InvalidCommandException("Command is invalid.");
        }
        return handlers.get(commandKey);
    }
}

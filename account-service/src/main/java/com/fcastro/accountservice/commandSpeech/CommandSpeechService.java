package com.fcastro.accountservice.commandSpeech;

import com.fcastro.accountservice.exception.InvalidCommandException;
import com.fcastro.commons.exception.RequestParamExpectedException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class CommandSpeechService {

    private final CommandHandlerRegistry handlerRegistry;

    private final Pattern commandPattern = Pattern.compile("^(?<command>\\S+)");

    public CommandSpeechService(CommandHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    public void execute(String commandTranscript) {
        if (commandTranscript == null) {
            throw new RequestParamExpectedException("Param commandTranscript is mandatory.");
        }

        var matcher = commandPattern.matcher(commandTranscript);
        if (!matcher.find()) {
            throw new InvalidCommandException("Command was not recognized.");
        }

        var commandName = matcher.group("command").toLowerCase();

        CommandHandler handler = handlerRegistry.getHandler(commandName);
        if (handler == null) {
            throw new IllegalArgumentException("No handler found for command: " + commandName);
        }

        handler.handle(commandTranscript);
    }
}

package com.fcastro.accountservice.commandSpeech;

public interface CommandHandler {
    String getCommandName();

    void handle(String data);
}

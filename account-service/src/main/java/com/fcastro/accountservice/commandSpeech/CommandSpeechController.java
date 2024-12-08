package com.fcastro.accountservice.commandSpeech;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accountservice/commandSpeech")
public class CommandSpeechController {

    private final CommandSpeechService commandSpeechService;

    public CommandSpeechController(CommandSpeechService commandSpeechService) {
        this.commandSpeechService = commandSpeechService;
    }

    @PostMapping
    public ResponseEntity executeCommand(@RequestBody CommandSpeechDto commandSpeechDto) {
        commandSpeechService.execute(commandSpeechDto.getTranscript());
        return ResponseEntity.noContent().build();
    }

}

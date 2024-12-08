package com.fcastro.accountservice.commandSpeech;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIMessage {

    private String role;
    private String content;
}

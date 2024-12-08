package com.fcastro.accountservice.commandSpeech;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIResponse {

    private List<Choice> choices;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {

        private int index;
        private OpenAIMessage message;
    }

}

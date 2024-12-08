package com.fcastro.accountservice.commandSpeech;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRequest {

    private String model;
    private List<OpenAIMessage> messages;
    //    private int n;
    private double temperature;

    public OpenAIRequest(String model, String systemPrompt, String userPrompt) {
        this.model = model;

        this.messages = new ArrayList<OpenAIMessage>();
        this.messages.add(new OpenAIMessage("system", systemPrompt));
        this.messages.add(new OpenAIMessage("user", userPrompt));
    }

}

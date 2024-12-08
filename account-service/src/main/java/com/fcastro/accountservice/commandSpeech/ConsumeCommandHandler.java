package com.fcastro.accountservice.commandSpeech;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcastro.accountservice.event.CommandEventProducer;
import com.fcastro.accountservice.exception.InvalidCommandException;
import com.fcastro.kafka.model.CommandEventDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ConsumeCommandHandler implements CommandHandler {

    @Qualifier("openaiRestTemplate")
    private final RestTemplate openaiRestTemplate;

    private final ObjectMapper objectMapper;

    private final CommandEventProducer commandEventProducer;


    private final String url = "https://api.openai.com/v1/chat/completions";

    private final String model = "gpt-3.5-turbo";

    private final String system = "You are a polyglot assistant. Your task is to transform commands into standardized JSON. Focus on the following:\n" +
            "- The JSON must have the format {quantity, product}.\n" +
            "- The \"quantity\" attribute is always an integer.\n" +
            "- The \"product\" attribute must only contain the product name without unit, packaging, or descriptors (e.g., \"caixas de leite\" becomes \"leite\").\n" +
            "- Do not translate the product name.";

    private final String user = "Transform the following command “${commandTranscript}” into a standardized JSON. \n" +
            "\n" +
            "### Rules:\n" +
            "- The JSON must have the format {quantity, product}.\n" +
            "- Extract the \"quantity\" as an integer.\n" +
            "- The \"product\" should only include the name and exclude unit or packaging descriptors.\n" +
            "\n" +
            "### Examples:\n" +
            "- Input: \"consumir 2 caixas de leite\"\n" +
            "  Output: {\"quantity\": 2, \"product\": \"leite\"}\n" +
            "\n" +
            "- Input: \"buy 5 bottles of water\"\n" +
            "  Output: {\"quantity\": 5, \"product\": \"water\"}\n" +
            "\n" +
            "Now process this command: “${commandTranscript}”.";


    public ConsumeCommandHandler(RestTemplate openaiRestTemplate, ObjectMapper objectMapper, CommandEventProducer commandEventProducer) {
        this.openaiRestTemplate = openaiRestTemplate;
        this.objectMapper = objectMapper;
        this.commandEventProducer = commandEventProducer;
    }

    @Override
    public String getCommandName() {
        return "consume";
    }

    @Override
    public void handle(String commandTranscript) {

        //get param and interpolate to build the prompt
        var userPrompt = user.replace("${commandTranscript}", commandTranscript);

        //create request
        var request = new OpenAIRequest(model, system, userPrompt);
        request.setTemperature(0.2); //This reduces creative variance in the response.

        //call OpenAI
        var response = openaiRestTemplate.postForObject(url, request, OpenAIResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new InvalidCommandException("Error processing command: OpenAi response is invalid.");
        }

        // return the first response
        var content = response.getChoices().get(0).getMessage().getContent();
        if (content == null) {
            throw new InvalidCommandException("Error processing command: OpenAi response is invalid.");
        }

        //json to Map<String, Object>
        Map<String, Object> params;
        try {
            params = objectMapper.readValue(content, Map.class);

        } catch (JsonProcessingException e) {
            throw new InvalidCommandException("Error processing command: OpenAi response is in an invalid format.");
        }

        //send command to kafka
        sendCommand(params);
    }

    private void sendCommand(Map<String, Object> params) {
        var commandEvent = CommandEventDto.builder()
                .command(getCommandName())
                .params(params)
                .build();

        commandEventProducer.send(commandEvent);
    }
}


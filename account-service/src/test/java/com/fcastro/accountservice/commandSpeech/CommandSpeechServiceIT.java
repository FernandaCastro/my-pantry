package com.fcastro.accountservice.commandSpeech;

import com.fcastro.accountservice.event.CommandEventProducer;
import com.fcastro.kafka.model.CommandEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {"spring.kafka.enabled=false"})
public class CommandSpeechServiceIT {

    @Autowired
    CommandSpeechService service;

    @MockBean
    CommandEventProducer commandEventProducer;

    @Autowired
    ConsumeCommandHandler consumeCommandHandler;

    @Autowired
    CommandHandlerRegistry commandHandlerRegistry;

    @Test
    public void givenConsumeCommand_whenExecute_shouldExecuteCommand() {

        //given
        String commandTranscript = "Consumir 2 caixas de leite";

        //when
        service.execute(commandTranscript);

        //then
        var params = new HashMap<String, Object>();
        params.put("quantity", 2);
        params.put("productCode", "leite");

        var commandEvent = CommandEventDto.builder()
                .command("consume")
                .params(params)
                .build();

        verify(commandEventProducer, times(1)).send(commandEvent);
    }
}

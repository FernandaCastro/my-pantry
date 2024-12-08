package com.fcastro.accountservice.event;

import com.fcastro.kafka.config.KafkaProperties;
import com.fcastro.kafka.event.CommandEvent;
import com.fcastro.kafka.model.CommandEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@Component
public class CommandEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandEventProducer.class);

    private final KafkaProperties kafkaProperties;

    private final KafkaTemplate<String, CommandEvent> kafkaTemplate;

    public CommandEventProducer(KafkaProperties kafkaProperties, KafkaTemplate<String, CommandEvent> kafkaTemplate) {
        this.kafkaProperties = kafkaProperties;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(CommandEventDto dto) {
        var event = CommandEvent.builder()
                .key(dto.getCommand()) //account:<email>
                .data(dto)
                .createdAt(ZonedDateTime.now())
                .build();

        CompletableFuture<SendResult<String, CommandEvent>> future = kafkaTemplate.send(kafkaProperties.getCommandTopic(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.info("Event Sent: Topic[{}], Partition[{}], Offset[{}], Timestamp[{}], Data[{}]",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp(),
                        event.getData().toString()
                );
            } else {
                LOGGER.error("Unable to send CommandEvent to {}: {}", kafkaProperties.getCommandTopic(), event.toString());
                //throw new KafkaException("Unable to send AccountEvent: " + ex.getMessage(), ex);
                //TODO: How to treat this async exception? Save to try again later? Kafka has it available?
            }
        });
    }
}

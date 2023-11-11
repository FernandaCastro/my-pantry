package com.fcastro.pantry.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.model.PurchaseCreateEvent;
import com.fcastro.kafka.model.PurchaseEventItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class EventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProducer.class);

    private final KafkaConfigData kafkaConfigData;

    private final KafkaTemplate<String, PurchaseCreateEvent> kafkaTemplate;

    public EventProducer(KafkaConfigData kafkaConfigData, KafkaTemplate<String, PurchaseCreateEvent> kafkaTemplate) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(PurchaseEventItemDto dto) {
        var event = PurchaseCreateEvent.builder().item(dto).build();

        CompletableFuture<SendResult<String, PurchaseCreateEvent>> future = kafkaTemplate.send(kafkaConfigData.getPurchaseCreateTopic(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.info("Event Sent: Topic[{}], Partition[{}], Offset[{}], Timestamp[{}], Data[{}]",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp(),
                        event.getItem().toString()
                );
            } else {
                LOGGER.error("Unable to send PurchaseCreateEvent to {}: {}", kafkaConfigData.getPurchaseCreateTopic(), event.toString());
                throw new KafkaException("Unable to send PurchaseCreateEvent: " + ex.getMessage(), ex);
            }
        });
    }
}

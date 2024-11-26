package com.fcastro.pantryservice.event;

import com.fcastro.kafka.config.KafkaProperties;
import com.fcastro.kafka.event.PurchaseEvent;
import com.fcastro.kafka.model.PurchaseEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@Component
public class PurchaseEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseEventProducer.class);

    private final KafkaProperties kafkaConfigData;

    private final KafkaTemplate<String, PurchaseEvent> kafkaTemplate;

    public PurchaseEventProducer(KafkaProperties kafkaConfigData, KafkaTemplate<String, PurchaseEvent> kafkaTemplate) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(PurchaseEventDto dto) {
        var event = PurchaseEvent.builder()
                .key("pantry:" + dto.getPantryId() + "-product:" + dto.getProductId())  //pantry:123-product:123
                .data(dto)
                .createdAt(ZonedDateTime.now(ZoneOffset.UTC))
                .build();

        CompletableFuture<SendResult<String, PurchaseEvent>> future = kafkaTemplate.send(kafkaConfigData.getPurchaseCreateTopic(), event);

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
                LOGGER.error("Unable to send PurchaseCreateEvent to {}: {}", kafkaConfigData.getPurchaseCreateTopic(), event.toString());
                //throw new KafkaException("Unable to send PurchaseCreateEvent: " + ex.getMessage(), ex);
                //TODO: How to treat this async exception? Save to try again later? Kafka has it available?
            }
        });
    }
}

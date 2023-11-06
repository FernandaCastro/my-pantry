package com.fcastro.pantry.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.model.PurchaseCreateEvent;
import com.fcastro.kafka.model.PurchaseEventItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
        var purchaseEvent = PurchaseCreateEvent.builder().item(dto).build();

        kafkaTemplate.send(kafkaConfigData.getPurchaseCreateTopic(), purchaseEvent);

        LOGGER.info("Sent to {}: {}", kafkaConfigData.getPurchaseCreateTopic(), purchaseEvent.getItem().toString());
    }
}

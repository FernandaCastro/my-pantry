package com.fcastro.purchase.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.model.PurchaseCompleteEvent;
import com.fcastro.kafka.model.PurchaseEventItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProducer.class);

    private final KafkaTemplate<String, PurchaseCompleteEvent> kafkaTemplate;

    private final KafkaConfigData kafkaConfigData;

    public EventProducer(KafkaTemplate<String, PurchaseCompleteEvent> kafkaTemplate, KafkaConfigData kafkaConfigData) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaConfigData = kafkaConfigData;
    }

    public void sendPurchaseCompleteEvent(List<PurchaseEventItemDto> items) {
        var event = PurchaseCompleteEvent.builder().items(items).build();
        kafkaTemplate.send(kafkaConfigData.getPurchaseCompleteTopic(), event);

        LOGGER.info("Sent to {}: {}", kafkaConfigData.getPurchaseCompleteTopic(), event.getItems().toString());
    }
}

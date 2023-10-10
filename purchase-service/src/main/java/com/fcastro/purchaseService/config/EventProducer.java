package com.fcastro.purchaseService.config;

import com.fcastro.events.ItemDto;
import com.fcastro.events.PurchaseCompleteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    private KafkaTemplate<String, PurchaseCompleteEvent> kafkaTemplate;

    public void sendPurchaseCompleteEvent(List<ItemDto> items) {
        var event = PurchaseCompleteEvent.builder().items(items).build();
        kafkaTemplate.send(EventConfig.PURCHASE_COMPLETE_TOPIC, event);

        LOGGER.info("Sent to {}: {}", EventConfig.PURCHASE_COMPLETE_TOPIC, event.getItems().toString());
    }
}

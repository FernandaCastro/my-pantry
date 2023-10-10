package com.fcastro.pantryService.config;

import com.fcastro.events.ItemDto;
import com.fcastro.events.PurchaseCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    private KafkaTemplate<String, PurchaseCreateEvent> kafkaTemplate;

    public void sendPurchaseCreateEvent(ItemDto dto) {
        var purchaseEvent = PurchaseCreateEvent.builder().item(dto).build();
        kafkaTemplate.send(EventConfig.PURCHASE_CREATE_TOPIC, purchaseEvent);
        LOGGER.info("Sent: {}", purchaseEvent.getItem().toString());
    }
}

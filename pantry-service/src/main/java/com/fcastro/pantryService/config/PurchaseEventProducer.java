package com.fcastro.pantryService.config;

import com.fcastro.events.PurchaseEvent;
import com.fcastro.events.PurchaseEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PurchaseEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseEventProducer.class);

    @Autowired
    private KafkaTemplate<String, PurchaseEvent> kafkaTemplate;

    public void send(PurchaseEventDto dto) {
        var purchaseEvent = PurchaseEvent.builder().item(dto).build();
        kafkaTemplate.send(PurchaseEventConfig.PURCHASE_TOPIC, purchaseEvent);
        LOGGER.info("Sent: {}", purchaseEvent.getItem().toString());
    }
}

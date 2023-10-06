package com.fcastro.pantryInventory.event;

import com.fcastro.pantryInventory.config.PurchaseItemEventConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PurchaseItemEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseItemEventProducer.class);

    @Autowired
    private KafkaTemplate<String, PurchaseItemDto> kafkaTemplate;

    public void send(PurchaseItemDto dto) {
        kafkaTemplate.send(PurchaseItemEventConfig.PURCHASE_ITEM_TOPIC, dto);
        LOGGER.info("Sent: {}", dto.toString());
    }
}

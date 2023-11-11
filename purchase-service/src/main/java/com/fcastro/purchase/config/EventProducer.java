package com.fcastro.purchase.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.model.PurchaseCompleteEvent;
import com.fcastro.kafka.model.PurchaseEventItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        CompletableFuture<SendResult<String, PurchaseCompleteEvent>> future = kafkaTemplate.send(kafkaConfigData.getPurchaseCompleteTopic(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.info("Event Sent: Topic[{}], Partition[{}], Offset[{}], Timestamp[{}], Data[{}]",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp(),
                        event.getItems().toString()
                );
            } else {
                LOGGER.error("Unable to send PurchaseCompleteEvent to {}: {}", kafkaConfigData.getPurchaseCompleteTopic(), event.toString());
                throw new KafkaException("Unable to send PurchaseCompleteEvent: " + ex.getMessage(), ex);
            }
        });
    }
}

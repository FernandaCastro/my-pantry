package com.fcastro.pantry.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.event.ProductEvent;
import com.fcastro.kafka.event.ProductEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ProductEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventProducer.class);

    private final KafkaConfigData kafkaConfigData;

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductEventProducer(KafkaConfigData kafkaConfigData, KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ProductEventDto dto) {
        var event = ProductEvent.builder().data(dto).build();

        CompletableFuture<SendResult<String, ProductEvent>> future = kafkaTemplate.send(kafkaConfigData.getProductTopic(), event);

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
                LOGGER.error("Unable to send ProductEvent to {}: {}", kafkaConfigData.getProductTopic(), event.toString());
                throw new KafkaException("Unable to send ProductEvent: " + ex.getMessage(), ex);
                //TODO: How to treat this async exception? Save to try again later? Kafka has it available?
            }
        });
    }
}

package com.fcastro.accountservice.event;

import com.fcastro.kafka.config.KafkaProperties;
import com.fcastro.kafka.event.AccountEvent;
import com.fcastro.kafka.model.AccountEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@Component
public class AccountEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountEventProducer.class);

    private final KafkaProperties kafkaProperties;

    private final KafkaTemplate<String, AccountEvent> kafkaTemplate;

    public AccountEventProducer(KafkaProperties kafkaProperties, KafkaTemplate<String, AccountEvent> kafkaTemplate) {
        this.kafkaProperties = kafkaProperties;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(AccountEventDto dto) {
        var event = AccountEvent.builder()
                .key("account:" + dto.getEmail()) //account:<email>
                .data(dto)
                .createdAt(ZonedDateTime.now())
                .build();

        CompletableFuture<SendResult<String, AccountEvent>> future = kafkaTemplate.send(kafkaProperties.getAccountTopic(), event);

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
                LOGGER.error("Unable to send AccoountEvent to {}: {}", kafkaProperties.getProductTopic(), event.toString());
                //throw new KafkaException("Unable to send AccountEvent: " + ex.getMessage(), ex);
                //TODO: How to treat this async exception? Save to try again later? Kafka has it available?
            }
        });
    }
}

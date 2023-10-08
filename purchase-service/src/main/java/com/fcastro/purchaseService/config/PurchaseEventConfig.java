package com.fcastro.purchaseService.config;

import com.fcastro.events.PurchaseEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

@Configuration
public class PurchaseEventConfig {

    public final static String PURCHASE_TOPIC = "purchaseTopic";
    private final String PANTRY_GROUP = "pantryGroup";

    @Bean
    public ConsumerFactory<String, PurchaseEvent> consumerFactory() {
        var purchaseEventDeserializer = new JsonDeserializer<>(PurchaseEvent.class);
        purchaseEventDeserializer.setRemoveTypeHeaders(false);
        purchaseEventDeserializer.addTrustedPackages("*");
        purchaseEventDeserializer.setUseTypeMapperForKey(true);

        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, PANTRY_GROUP);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), purchaseEventDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, PurchaseEvent>> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, PurchaseEvent>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public NewTopic purchaseTopic() {
        return TopicBuilder.name(PURCHASE_TOPIC)
                .build();
    }
}

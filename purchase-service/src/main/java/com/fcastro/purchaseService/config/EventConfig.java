package com.fcastro.purchaseService.config;

import com.fcastro.events.PurchaseCompleteEvent;
import com.fcastro.events.PurchaseCreateEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;

@Configuration
public class EventConfig {

    public final static String PURCHASE_CREATE_TOPIC = "purchaseCreateTopic";
    public final static String PURCHASE_COMPLETE_TOPIC = "purchaseCompleteTopic";
    private final String PANTRY_GROUP = "pantryGroup";

    //Consumer
    @Bean
    public ConsumerFactory<String, PurchaseCreateEvent> consumerFactory() {
        var purchaseEventDeserializer = new JsonDeserializer<>(PurchaseCreateEvent.class);
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
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, PurchaseCreateEvent>> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, PurchaseCreateEvent>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public NewTopic purchaseTopic() {
        return TopicBuilder.name(PURCHASE_CREATE_TOPIC).build();
    }

    //Producer
    @Bean
    public ProducerFactory<String, PurchaseCompleteEvent> producerFactory() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, PANTRY_GROUP);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, PurchaseCompleteEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic purchaseCompleteTopic() {
        return TopicBuilder.name(PURCHASE_COMPLETE_TOPIC).build();
    }
}

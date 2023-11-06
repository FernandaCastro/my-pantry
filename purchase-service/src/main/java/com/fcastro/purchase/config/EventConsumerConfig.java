package com.fcastro.purchase.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.model.PurchaseCreateEvent;
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
public class EventConsumerConfig {

    private final KafkaConfigData kafkaConfigData;

    public EventConsumerConfig(KafkaConfigData kafkaConfigData) {
        this.kafkaConfigData = kafkaConfigData;
    }

    //Consumer
    @Bean
    public ConsumerFactory<String, PurchaseCreateEvent> consumerFactory() {
        var purchaseEventDeserializer = new JsonDeserializer<>(PurchaseCreateEvent.class);
        purchaseEventDeserializer.setRemoveTypeHeaders(false);
        purchaseEventDeserializer.addTrustedPackages("*");
        purchaseEventDeserializer.setUseTypeMapperForKey(true);

        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServersConfig());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigData.getPantryGroup());

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), purchaseEventDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, PurchaseCreateEvent>> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, PurchaseCreateEvent>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public NewTopic purchaseCreateTopic() {
        return TopicBuilder.name(kafkaConfigData.getPurchaseCreateTopic()).build();
    }
}

package com.fcastro.pantry.config;

import com.fcastro.kafka.config.KafkaConfigData;
import com.fcastro.kafka.model.PurchaseCreateEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;

@Configuration
public class EventProducerConfig {

    private final KafkaConfigData kafkaConfigData;

    public EventProducerConfig(KafkaConfigData kafkaConfigData) {
        this.kafkaConfigData = kafkaConfigData;
    }

    @Bean
    public ProducerFactory<String, PurchaseCreateEvent> producerFactory() {
        var props = new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServersConfig());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigData.getPantryGroup());
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, PurchaseCreateEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic purchaseCreateTopic() {
        return TopicBuilder.name(kafkaConfigData.getPurchaseCreateTopic()).build();
    }
}

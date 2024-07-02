package com.fcastro.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.Serializable;
import java.util.HashMap;

@Configuration
@ConditionalOnProperty(prefix = "spring", value = "kafka.enabled", matchIfMissing = true, havingValue = "true")
public class KafkaEventConfig<V extends Serializable> {

    private final KafkaConfigData kafkaConfigData;

    private HashMap<String, Object> factoryProperties;

    public KafkaEventConfig(KafkaConfigData kafkaConfigData) {
        this.kafkaConfigData = kafkaConfigData;
        setFactoryProperties();
    }

    private void setFactoryProperties() {
        factoryProperties = new HashMap<String, Object>();
        factoryProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServersConfig());
        factoryProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        factoryProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        factoryProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigData.getGroup());
    }

    @Bean
    public ConsumerFactory consumerFactory() {

        var eventDeserializer = new JsonDeserializer();
        eventDeserializer.setRemoveTypeHeaders(false);
        eventDeserializer.addTrustedPackages("*");
        eventDeserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(factoryProperties, new StringDeserializer(), eventDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, V>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, V> producerFactory() {
        var eventSerializer = new JsonSerializer();
        eventSerializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaProducerFactory<>(factoryProperties, new StringSerializer(), eventSerializer);
    }

    @Bean
    public KafkaTemplate<String, V> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic purchaseCompleteTopic() {
        return TopicBuilder.name(kafkaConfigData.getPurchaseCompleteTopic())
                .partitions(kafkaConfigData.getPartitions())
                .replicas(kafkaConfigData.getReplicas())
                .build();
    }

    @Bean
    public NewTopic purchaseCreateTopic() {
        return TopicBuilder.name(kafkaConfigData.getPurchaseCreateTopic())
                .partitions(kafkaConfigData.getPartitions())
                .replicas(kafkaConfigData.getReplicas())
                .build();
    }

    @Bean
    public NewTopic productTopic() {
        return TopicBuilder.name(kafkaConfigData.getProductTopic())
                .partitions(kafkaConfigData.getPartitions())
                .replicas(kafkaConfigData.getReplicas())
                .build();
    }
}

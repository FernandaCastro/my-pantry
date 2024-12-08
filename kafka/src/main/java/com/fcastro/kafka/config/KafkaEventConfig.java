package com.fcastro.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
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
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.io.Serializable;
import java.util.HashMap;

@Configuration
@ConditionalOnProperty(prefix = "spring", value = "kafka.enabled", matchIfMissing = true, havingValue = "true")
public class KafkaEventConfig<V extends Serializable> {

    private final KafkaProperties kafkaConfigData;

    private HashMap<String, Object> producerFactoryProperties;
    private HashMap<String, Object> consumerFactoryProperties;

    public KafkaEventConfig(KafkaProperties kafkaConfigData) {
        this.kafkaConfigData = kafkaConfigData;
        setProducerFactoryProperties();
        setConsumerFactoryProperties();
    }

    private void setProducerFactoryProperties() {
        producerFactoryProperties = new HashMap<String, Object>();
        producerFactoryProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServersConfig());
        producerFactoryProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        producerFactoryProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        producerFactoryProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        //ACKS= 1 (ack only by the leader)
        //ACKS= all or -1 (the leader waits for the full set of in-sync replicas to acknowledge the record.)
    }

    private void setConsumerFactoryProperties() {
        consumerFactoryProperties = new HashMap<String, Object>();
        consumerFactoryProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServersConfig());
        consumerFactoryProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerFactoryProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        //Isolates the consumption. Consumers with the same groupId will consume events parallelly
        consumerFactoryProperties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigData.getGroup());

        consumerFactoryProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        //consumerFactoryProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    @Bean
    public ConsumerFactory consumerFactory() {

        var eventDeserializer = new JsonDeserializer();
        eventDeserializer.setRemoveTypeHeaders(false);
        eventDeserializer.addTrustedPackages("*");
        eventDeserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(consumerFactoryProperties, new StringDeserializer(), eventDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, V>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public ProducerFactory<String, V> producerFactory() {
        var eventSerializer = new JsonSerializer();
        eventSerializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaProducerFactory<>(producerFactoryProperties, new StringSerializer(), eventSerializer);
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

    @Bean
    public NewTopic accountTopic() {
        return TopicBuilder.name(kafkaConfigData.getAccountTopic())
                .partitions(kafkaConfigData.getPartitions())
                .replicas(kafkaConfigData.getReplicas())
                .build();
    }
}

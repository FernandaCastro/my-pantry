package com.fcastro.kafka.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
@ConfigurationPropertiesScan
public class KafkaConfigData {

    private String bootstrapServersConfig;

    private String purchaseCreateTopic;
    private String purchaseCompleteTopic;
    private String productTopic;

    private String group;

    /* For now, it will configure all topics with same values */
    private Integer partitions;
    private Short replicas;

}

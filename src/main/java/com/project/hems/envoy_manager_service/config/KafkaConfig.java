package com.project.hems.envoy_manager_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "property.config.kafka")
@Setter
public class KafkaConfig {

    private String rawEnergyTopic;
    private String dispatchEnergyTopic;
    private Integer rawEnergyPartitionCount;
    private Integer dispatchEnergyPartitionCount;
    private Integer replicaCount;

    @Bean
    public NewTopic rawEnergyReadings() {
        log.info("Creating Kafka topic {}", rawEnergyTopic);
        return TopicBuilder.name(rawEnergyTopic)
                .partitions(rawEnergyPartitionCount)
                .replicas(replicaCount)
                .build();
    }

    @Bean
    public NewTopic energyDispatchCommands() {
        log.info("Creating Kafka topic {}", dispatchEnergyTopic);
        return TopicBuilder.name(dispatchEnergyTopic)
                .partitions(dispatchEnergyPartitionCount)
                .replicas(replicaCount)
                .build();
    }
}

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
    private Integer partitionCount;
    private Integer replicaCount;

    @Bean
    public NewTopic getTopic() {
        log.trace("topic name from env = " + rawEnergyTopic);
        log.trace("partition count from env = " + partitionCount);
        log.trace("replica count from env = " + replicaCount);
        return TopicBuilder.name(rawEnergyTopic)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .build();
    }

}

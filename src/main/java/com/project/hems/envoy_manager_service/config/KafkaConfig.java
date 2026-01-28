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
    private String siteCreationTopic;
    private Integer rawEnergypartitionCount;
    private Integer dispatchEnergypartitionCount;
    private Integer siteCreationpartitionCount;
    private Integer replicaCount;

    @Bean
    public NewTopic rawEnergyReadings() {
        return TopicBuilder.name(rawEnergyTopic)
                .partitions(rawEnergypartitionCount)
                .replicas(replicaCount)
                .build();
    }

    @Bean
    public NewTopic energyDispatchCommands() {
        return TopicBuilder.name(dispatchEnergyTopic)
                .partitions(dispatchEnergypartitionCount)
                .replicas(replicaCount)
                .build();
    }

    @Bean
    public NewTopic siteCreationTopic(){
        return TopicBuilder.name(siteCreationTopic)
                .partitions(siteCreationpartitionCount)
                .replicas(replicaCount)
                .build();
    }

}

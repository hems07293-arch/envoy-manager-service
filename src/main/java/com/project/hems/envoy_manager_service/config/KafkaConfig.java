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

    private String topic;

    @Bean
    public NewTopic getTopic() {
        log.info("Topic name from env = " + topic);
        return TopicBuilder.name(topic)
                .partitions(10)
                .replicas(1)
                .build();
    }

}

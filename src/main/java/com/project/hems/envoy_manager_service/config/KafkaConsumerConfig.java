package com.project.hems.envoy_manager_service.config;

import com.project.hems.envoy_manager_service.model.simulator.MeterSnapshot;
import com.project.hems.envoy_manager_service.model.site.SiteCreationEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, MeterSnapshot> meterSnapshotConsumerFactory() {
        JsonDeserializer<MeterSnapshot> deserializer =
                new JsonDeserializer<>(MeterSnapshot.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);


        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MeterSnapshot>
    meterSnapshotKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, MeterSnapshot> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(meterSnapshotConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, SiteCreationEvent> siteCreationConsumerFactory() {
        JsonDeserializer<SiteCreationEvent> deserializer =
                new JsonDeserializer<>(SiteCreationEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);


        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SiteCreationEvent>
    siteCreationKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, SiteCreationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(siteCreationConsumerFactory());
        return factory;
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}

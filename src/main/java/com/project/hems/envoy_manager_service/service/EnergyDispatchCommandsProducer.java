package com.project.hems.envoy_manager_service.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.model.DispatchMode;
import com.project.hems.envoy_manager_service.model.EnergyDispatchCommand;
import com.project.hems.envoy_manager_service.model.SourceType;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Setter
@ConfigurationProperties(prefix = "property.config.kafka")
@RequiredArgsConstructor
public class EnergyDispatchCommandsProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private String energyDispatchTopic;

    @Scheduled(fixedRate = 5000)
    public void simulateDummyDispatchCommands() {
        log.info("simulateDummyDispatchCommands: simulating dummy dispatch commands from envoy");

        log.debug("creating a dummy pojo for energy dispacth command");
        EnergyDispatchCommand command = EnergyDispatchCommand.builder()
                .siteId(101l)
                .sourceType(SourceType.SOLAR)
                .dispatchMode(DispatchMode.INSTANT)
                .energyAmountKwh(1.2)
                .build();

        log.debug("dummy pojo created for dispatch command = " + command);

        kafkaTemplate.send(energyDispatchTopic, command);

    }

}

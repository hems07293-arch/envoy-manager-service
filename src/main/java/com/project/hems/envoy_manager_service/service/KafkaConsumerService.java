package com.project.hems.envoy_manager_service.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.model.SiteControlCommand;
import com.project.hems.envoy_manager_service.model.dispatch.DispatchEvent;
import com.project.hems.envoy_manager_service.model.simulator.MeterSnapshot;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Setter
@ConfigurationProperties(prefix = "property.config.kafka")
public class KafkaConsumerService {

    private String rawEnergyTopic;
    private String dispatchEnergyTopic;
    private final MeterAggregationService meterAggregationService;
    private final CommandTranslatorService commandTranslatorService;
    private final SimulationConnectorService connectorService;
    // private final SimpMessagingTemplate webSocket; // For UI Live Stream

    @KafkaListener(topics = "${property.config.kafka.raw-energy-topic}", groupId = "${spring.kafka.consumer.raw-energy-group-id}")
    public void consumeRawMeterReadings(MeterSnapshot meterSnapshot) {
        log.info("consumeRawMeterReadings: consuming raw meter reading data from kafka with topic = " + rawEnergyTopic);

        // TODO: if want to send refined data directly to UI, implement this
        // Push directly to Frontend via WebSocket topic
        // webSocket.convertAndSend("/topic/meter/" + meterSnapshot.getMeterId(),
        // meterSnapshot);

        // Send to aggregator to wait for batching
        log.debug("consumeRawMeterReadings: send raw data for processing to aggregator" + meterSnapshot);
        meterAggregationService.process(meterSnapshot);
    }

    @KafkaListener(topics = "${property.config.kafka.dispatch-energy-topic}", groupId = "${property.config.kafka.dispatch-energy-group-id}")
    public void consumeDispatchEvents(DispatchEvent dispatchEvent) {
        log.info(
                "consumeDispatchEvents: consuming all energy dispatch commands from dispatch manager with topic = "
                        + dispatchEnergyTopic);

        SiteControlCommand siteControlCommand = commandTranslatorService.translateDispatchEvent(dispatchEvent);

        connectorService.applyControlToSimulation(siteControlCommand);
    }

}

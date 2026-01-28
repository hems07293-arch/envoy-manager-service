package com.project.hems.envoy_manager_service.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.model.SiteControlCommand;
import com.project.hems.envoy_manager_service.model.dispatch.DispatchEvent;
import com.project.hems.envoy_manager_service.model.simulator.MeterSnapshot;
import com.project.hems.envoy_manager_service.model.site.SiteCreationEvent;

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
    private String siteCreationTopic;

    private final CommandTranslatorService commandTranslatorService;
    private final SimulationConnectorService connectorService;
    private final MeterCreationService meterCreationService;
    // private final SimpMessagingTemplate webSocket; // For UI Live Stream

    @KafkaListener(
            topics = "${property.config.kafka.raw-energy-topic}",
            groupId = "${property.config.kafka.raw-energy-group-id}",
            containerFactory = "meterSnapshotKafkaListenerFactory"
    )
    public void consumeRawMeterReadings(MeterSnapshot meterSnapshot) {

        log.info(
                "consumeRawMeterReadings: received raw meter snapshot from topic={} siteId={} meterId={} timestamp={}",
                rawEnergyTopic,
                meterSnapshot.getSiteId(),
                meterSnapshot.getMeterId(),
                meterSnapshot.getTimestamp());

        log.debug(
                "consumeRawMeterReadings: payload={}",
                meterSnapshot);

        // TODO: if want to send refined data directly to UI, implement this
        // webSocket.convertAndSend("/topic/meter/" + meterSnapshot.getMeterId(),
        // meterSnapshot);

        // log.debug("consumeRawMeterReadings: forwarding raw snapshot to aggregation
        // service");
        // meterAggregationService.process(meterSnapshot);
    }

    @KafkaListener(topics = "${property.config.kafka.dispatch-energy-topic}", groupId = "${property.config.kafka.dispatch-energy-group-id}")
    public void consumeDispatchEvents(DispatchEvent dispatchEvent) {

        log.info(
                "consumeDispatchEvents: received dispatch command from topic={} dispatchId={} siteId={} eventType={}",
                dispatchEnergyTopic,
                dispatchEvent.getDispatchId(),
                dispatchEvent.getSiteId(),
                dispatchEvent.getEventType());

        log.debug(
                "consumeDispatchEvents: raw dispatch payload={}",
                dispatchEvent);

        SiteControlCommand siteControlCommand = commandTranslatorService.translateDispatchEvent(dispatchEvent);

        log.debug(
                "consumeDispatchEvents: translated dispatch command for siteId={} command={}",
                dispatchEvent.getSiteId(),
                siteControlCommand);

        connectorService.applyControlToSimulation(siteControlCommand);

        log.info(
                "consumeDispatchEvents: successfully applied control command to simulation for siteId={}",
                dispatchEvent.getSiteId());
    }

    @KafkaListener(
            topics = "${property.config.kafka.site-creation-topic}",
            groupId = "${property.config.kafka.site-creation-group-id}",
            containerFactory = "siteCreationKafkaListenerFactory"
    )
    public void consumeSiteCreationEvents(SiteCreationEvent siteCreationEvent) {

        log.info(
                "consumeSiteCreationEvents: received site creation event from topic={} siteId={} batteryCapacityWh={}",
                siteCreationTopic,
                siteCreationEvent.getSiteId(),
                siteCreationEvent.getBatteryCapacityW());

        log.debug(
                "consumeSiteCreationEvents: payload={}",
                siteCreationEvent);

        meterCreationService.createMeter(
                siteCreationEvent.getSiteId(),
                siteCreationEvent.getBatteryCapacityW());

        log.info(
                "consumeSiteCreationEvents: meter successfully created for siteId={}",
                siteCreationEvent.getSiteId());
    }

}

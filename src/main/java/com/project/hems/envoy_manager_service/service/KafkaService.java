package com.project.hems.envoy_manager_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.model.MeterSnapshot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaService {

    @KafkaListener(topics = "${property.config.kafka.raw-energy-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(MeterSnapshot meterSnapshot) {
        log.info("KafkaService: received the simulation info = " + meterSnapshot);
        System.out.println("Meter Snapshot got from simulator service = " + meterSnapshot);
    }
}

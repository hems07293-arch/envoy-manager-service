package com.project.hems.envoy_manager_service.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeterCreationService {

    private final SimulatorFeignClientService feignClientService;

    public void createMeter(Long siteId, Double batteryCapacityW) {
        feignClientService.activateMeterData(siteId, batteryCapacityW);
    }
}

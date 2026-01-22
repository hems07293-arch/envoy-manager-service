package com.project.hems.envoy_manager_service.service;

import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.model.SiteControlCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationConnectorService {
    private final SimulatorFeignClientService simulatorFeignClientService;

    public void applyControlToSimulation(SiteControlCommand command) {
        Long siteId = command.getSiteId();

        try {
            log.info("Applying Dispatch Control to Meter {}", siteId);

            // TODO: create a controller on simulator side for all these methods priority

            // Single Call to Update Simulation Settings
            // Payload: { "mode": "FORCE_DISCHARGE", "maxDischargeW": 2000 ... }
            simulatorFeignClientService.applyDispatch(command);

            log.info("Successfully reconfigured Simulation for Dispatch ID: {}", command.getDispatchId());

        } catch (

        Exception e) {
            log.error("Failed to push config to Simulation Service: " + e.getMessage());
            // Optional: Retry logic or alert Dispatch Manager of failure
        }
    }
}

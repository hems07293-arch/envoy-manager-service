package com.project.hems.envoy_manager_service.service;

import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.model.SiteControlCommand;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SimulationConnectorService {
    private SimulatorFeignClientService simulatorFeignClientService;

    public void applyControlToSimulation(SiteControlCommand command) {
        Long siteId = command.getSiteId();

        try {
            log.info("Applying Dispatch Control to Meter {}", siteId);

            // TODO: create a controller on simulator side for all these methods
            // priority
            // CALL 1: Change Energy Priority
            // Payload: ["SOLAR", "GRID", "BATTERY"]
            simulatorFeignClientService.changePriorityOfSite(siteId, command.getEnergyPriority());
            log.debug("Updated Energy Priority");

            // CALL 2: Update Battery Settings
            // Payload: { "mode": "FORCE_DISCHARGE", "maxDischargeW": 2000 ... }

            // CALL 3: Update Grid Constraints
            // Payload: { "allowExport": true, "maxExportW": 5000 ... }

            log.info("Successfully reconfigured Simulation for Dispatch ID: {}", command.getDispatchId());

        } catch (Exception e) {
            log.error("Failed to push config to Simulation Service: " + e.getMessage());
            // Optional: Retry logic or alert Dispatch Manager of failure
        }
    }
}

package com.project.hems.envoy_manager_service.service;

import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.model.SiteControlCommand;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SimulationConnectorService {

    public void applyControlToSimulation(SiteControlCommand command) {
        Long meterId = command.getMeterId();
        String baseUrl = "simulatorBaseUrl" + "/api/sim/control/" + meterId;

        try {
            log.info("Applying Dispatch Control to Meter {}", meterId);

            // TODO: create a controller on simulator side for all these methods
            // priority
            // CALL 1: Change Energy Priority
            // Payload: ["SOLAR", "GRID", "BATTERY"]
            String priorityUrl = baseUrl + "/priority";
            log.debug("Updated Energy Priority");

            // CALL 2: Update Battery Settings
            // Payload: { "mode": "FORCE_DISCHARGE", "maxDischargeW": 2000 ... }
            String batteryUrl = baseUrl + "/battery";
            log.debug("Updated Battery Control");

            // CALL 3: Update Grid Constraints
            // Payload: { "allowExport": true, "maxExportW": 5000 ... }
            String gridUrl = baseUrl + "/grid";
            log.debug("Updated Grid Limits");

            log.info("Successfully reconfigured Simulation for Dispatch ID: {}", command.getDispatchId());

        } catch (Exception e) {
            log.error("Failed to push config to Simulation Service: " + e.getMessage());
            // Optional: Retry logic or alert Dispatch Manager of failure
        }
    }
}

package com.project.hems.envoy_manager_service.web.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.project.hems.envoy_manager_service.domain.MeterHistory;
import com.project.hems.envoy_manager_service.model.LiveDashboardDTO;
import com.project.hems.envoy_manager_service.model.simulator.MeterSnapshot;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LiveStreamController {

    private final SimpMessagingTemplate messagingTemplate;

    // Call this method from your Kafka Listener (Hot Path)
    public void pushDashboardUpdate(MeterSnapshot raw, MeterHistory midnightSnapshot) {

        // 1. Calculate Energy Independence
        // Formula: 1 - (GridImport / TotalConsumption)
        double independence = 0.0;
        if (raw.getTotalHomeUsageKwh() > 0) {
            double gridImp = raw.getTotalGridImportKwh() - midnightSnapshot.getEndGridImportKwh();
            double totalCons = raw.getTotalHomeUsageKwh() - midnightSnapshot.getEndHomeUsageKwh();
            independence = (1.0 - (gridImp / totalCons)) * 100.0;
        }

        // 2. Build DTO
        LiveDashboardDTO dto = LiveDashboardDTO.builder()
                // Instant Power (for animations)
                .currentSolarPowerW(raw.getSolarProductionW())
                .currentGridPowerW(raw.getGridPowerW())

                // Daily Energy Math: Current - Midnight
                .solarProducedTodayKwh(raw.getTotalSolarYieldKwh() - midnightSnapshot.getEndSolarKwh())
                .gridImportedTodayKwh(raw.getTotalGridImportKwh() - midnightSnapshot.getEndGridImportKwh())
                .gridExportedTodayKwh(raw.getTotalGridExportKwh() - midnightSnapshot.getEndGridExportKwh())
                // ... (repeat for others) ...

                .energyIndependencePercent((int) independence)
                .batterySoC((double) raw.getBatterySoc())
                .build();

        // 3. Push to specific Site Topic
        messagingTemplate.convertAndSend("/topic/site/" + raw.getSiteId(), dto);
    }
}
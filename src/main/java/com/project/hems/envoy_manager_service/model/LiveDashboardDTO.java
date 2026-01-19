package com.project.hems.envoy_manager_service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveDashboardDTO {
    // --- INSTANT STATUS (For animations/color rings) ---
    private boolean isGridActive;
    private Double currentSolarPowerW; // For live pulsing animation
    private Double currentBatteryPowerW;
    private Double currentGridPowerW;
    private Double currentLoadPowerW;

    // --- DAILY TOTALS (The Text inside the Bubbles) ---
    // Calculated as: (Current Accumulator - Midnight Accumulator)
    private Double solarProducedTodayKwh; // "1.7 kWh Produced"
    private Double homeConsumedTodayKwh; // "3.5 kWh Consumed"
    private Double gridImportedTodayKwh; // "0.5 kWh Imported"
    private Double gridExportedTodayKwh; // "0.3 kWh Exported"
    private Double batteryDischargedTodayKwh; // "2.3 kWh Discharged"
    private Double batteryChargedTodayKwh; // "0.8 kWh Charged"

    // --- BATTERY STATUS ---
    private Double batterySoC; // "66%"
    private String batteryStatus; // "Charging" / "Discharging" / "Idle"
    private Integer energyIndependencePercent; // "85%"
}

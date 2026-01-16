package com.project.hems.envoy_manager_service.model.simulator;

public enum ChargingStatus {
    FULL,
    CHARGING, // Solar/Grid -> Battery
    DISCHARGING, // Battery -> Home/Grid
    IDLE, // Battery full or disconnected
    EMPTY // SoC at 0%
}

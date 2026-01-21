package com.project.hems.envoy_manager_service.model;

import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteControlCommand {

    private Long dispatchId;
    private Long siteId;
    private Long meterId;

    private Instant timestamp;
    private Instant validUntil;

    private List<EnergyPriority> energyPriority;

    private BatteryControl batteryControl;

    private GridControl gridControl;

    private String reason;

}

package com.project.hems.envoy_manager_service.model.dispatch;

import java.util.Set;

import com.project.hems.envoy_manager_service.model.EnergyPriority;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class DispatchEvent {
    private Long dispatchId;
    private Long siteId;
    private DispatchEventType eventType;
    private Long powerReqW;
    private Long durationSec;
    private Set<EnergyPriority> energyPriority;
    private String reason;
}

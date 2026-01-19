package com.project.hems.envoy_manager_service.model;

import com.project.hems.envoy_manager_service.model.simulator.BatteryMode;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class BatteryControl {
    private BatteryMode mode;
    private Long maxChargeW;
    private Long maxDischargeW;
    private Double minSocPercent;
    private Double maxSocPercent;
}

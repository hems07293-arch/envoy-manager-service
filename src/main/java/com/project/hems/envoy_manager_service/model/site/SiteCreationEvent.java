package com.project.hems.envoy_manager_service.model.site;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class SiteCreationEvent {

    private Long siteId;
    private Double batteryCapacityW;
}

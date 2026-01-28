package com.project.hems.envoy_manager_service.model.site;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class SiteCreationEvent {

    private UUID siteId;
    private Double batteryCapacityW;
}

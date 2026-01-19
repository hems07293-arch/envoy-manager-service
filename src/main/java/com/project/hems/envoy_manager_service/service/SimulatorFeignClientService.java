package com.project.hems.envoy_manager_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "simulator-service-testing", url = "http://localhost:9010")
public interface SimulatorFeignClientService {
    public void activateMeterData(@PathVariable Long siteId, @RequestBody Double batteryCapacity);
}

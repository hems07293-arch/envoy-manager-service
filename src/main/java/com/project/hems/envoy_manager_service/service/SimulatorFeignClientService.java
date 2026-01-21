package com.project.hems.envoy_manager_service.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.hems.envoy_manager_service.model.EnergyPriority;

@FeignClient(name = "simulator-service-testing", url = "http://localhost:9010")
public interface SimulatorFeignClientService {

    @PostMapping("/activate-meter/{siteId}")
    public void activateMeterData(@PathVariable("siteId") Long siteId, @RequestBody Double batteryCapacity);

    @PutMapping("/change-priority/{siteId}")
    public void changePriorityOfSite(@PathVariable("siteId") Long siteId,
            @RequestBody List<EnergyPriority> energyPriorities);
}

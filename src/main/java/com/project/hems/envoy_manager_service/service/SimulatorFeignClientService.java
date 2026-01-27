package com.project.hems.envoy_manager_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.hems.envoy_manager_service.model.EnergyPriority;
import com.project.hems.envoy_manager_service.model.SiteControlCommand;
import com.project.hems.envoy_manager_service.model.simulator.BatteryMode;
import com.project.hems.envoy_manager_service.model.simulator.MeterSnapshot;

@FeignClient(name = "simulator-service-testing", url = "http://localhost:9010", path = "/api/v1/simulation")
public interface SimulatorFeignClientService {

    @PostMapping("/activate-meter/{siteId}")
    public void activateMeterData(@PathVariable("siteId") UUID siteId, @RequestBody Double batteryCapacity);

    @PutMapping("/change-priority/{siteId}")
    public void changePriorityOfSite(@PathVariable("siteId") Long siteId,
            @RequestBody List<EnergyPriority> energyPriorities);

    @PutMapping("/change-battery-mode/{siteId}")
    public void changeBatteryMode(@PathVariable("siteId") Long siteId, @RequestBody BatteryMode batteryMode);

    @GetMapping("/get-meter-data/{siteId}")
    public ResponseEntity<MeterSnapshot> getMeterData(@PathVariable("siteId") Long siteId);

    @PostMapping("/dispatch")
    public ResponseEntity<Void> applyDispatch(@RequestBody SiteControlCommand command);
}

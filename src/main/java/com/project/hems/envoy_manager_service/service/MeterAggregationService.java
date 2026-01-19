package com.project.hems.envoy_manager_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.domain.MeterHistory;
import com.project.hems.envoy_manager_service.model.simulator.MeterSnapshot;
import com.project.hems.envoy_manager_service.repository.MeterHistoryRepository;
import com.project.hems.envoy_manager_service.web.controller.LiveStreamController;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Setter
@ConfigurationProperties(prefix = "property.config.meter-history")
public class MeterAggregationService {

    private final MeterHistoryRepository repository;
    private final LiveStreamController liveStreamController;

    // TODO: implement redis over here instead of this map
    // Temporary Buffer: Map<MeterId, List<RawReadings>>
    private final Map<Long, List<MeterSnapshot>> memoryBuffer = new ConcurrentHashMap<>();

    // Config: Aggregate every BATCH_SIZE readings
    private int BATCH_SIZE;

    public void process(MeterSnapshot rawData) {
        log.debug(
                "process: received snapshot for meterId = {}, siteId = {}",
                rawData.getMeterId(),
                rawData.getSiteId());

        // 1. Add to buffer
        memoryBuffer
                .computeIfAbsent(rawData.getMeterId(), k -> {
                    log.debug("process: creating new buffer for meterId = {}", rawData.getMeterId());
                    return new ArrayList<>();
                })
                .add(rawData);

        // 2. Check if bucket is full
        List<MeterSnapshot> bucket = memoryBuffer.get(rawData.getMeterId());

        log.debug(
                "process: buffer size for meterId = {} is {}",
                rawData.getMeterId(),
                bucket.size());

        if (bucket.size() >= BATCH_SIZE) {
            log.info(
                    "process: batch size reached for meterId = {}, aggregating {} records",
                    rawData.getMeterId(),
                    bucket.size());

            // 3. AGGREGATE & SAVE
            MeterHistory savedAggregate = saveAggregate(rawData.getMeterId(), rawData.getSiteId(), bucket);

            // send the raw data to UI using web socket
            liveStreamController.pushDashboardUpdate(rawData, savedAggregate);

            // 4. CLEAR BUFFER
            bucket.clear();
            log.debug("process: buffer cleared for meterId = {}", rawData.getMeterId());
        }
    }

    private MeterHistory saveAggregate(Long meterId, Long siteId, List<MeterSnapshot> bucket) {
        log.debug(
                "saveAggregate: invoked for meterId = {}, siteId = {}, bucketSize = {}",
                meterId,
                siteId,
                bucket.size());

        if (bucket.isEmpty()) {
            log.debug("saveAggregate: bucket is empty, skipping aggregation");
            return new MeterHistory();
        }

        // 1. Calculate AVERAGES for Instant Power (Watts)
        double avgSolar = bucket.stream()
                .mapToDouble(MeterSnapshot::getSolarProductionW)
                .average()
                .orElse(0.0);

        double avgLoad = bucket.stream()
                .mapToDouble(MeterSnapshot::getHomeConsumptionW)
                .average()
                .orElse(0.0);

        double avgBatteryW = bucket.stream()
                .mapToDouble(MeterSnapshot::getBatteryPowerW)
                .average()
                .orElse(0.0);

        double avgGridW = bucket.stream()
                .mapToDouble(MeterSnapshot::getGridPowerW)
                .average()
                .orElse(0.0);

        log.debug("saveAggregate: avgGridW W = " + bucket.stream()
                .mapToDouble(MeterSnapshot::getGridPowerW)
                .average());

        log.debug(
                "saveAggregate: avgSolarW = {}, avgLoadW = {}, avgBatteryW = {}, avgGridW = {}",
                avgSolar,
                avgLoad,
                avgBatteryW,
                avgGridW);

        // 2. Calculate Battery Health Stats
        DoubleSummaryStatistics socStats = bucket.stream()
                .mapToDouble(MeterSnapshot::getBatterySoc)
                .summaryStatistics();

        log.debug(
                "saveAggregate: battery SoC stats avg = {}, min = {}, max = {}",
                socStats.getAverage(),
                socStats.getMin(),
                socStats.getMax());

        // 3. Get SNAPSHOTS for Accumulators (kWh)
        MeterSnapshot lastRecord = bucket.get(bucket.size() - 1);

        log.debug(
                "saveAggregate: using last snapshot timestamp = {}",
                lastRecord.getTimestamp());

        // 4. Map to Entity
        MeterHistory history = new MeterHistory();
        history.setMeterId(meterId);
        history.setSiteId(lastRecord.getSiteId());
        history.setTimestamp(LocalDateTime.now());

        history.setAvgSolarW(avgSolar);
        history.setAvgConsumptionW(avgLoad);
        history.setAvgBatteryW(avgBatteryW);
        history.setAvgGridW(avgGridW);

        history.setEndSolarKwh(lastRecord.getTotalSolarYieldKwh());
        history.setEndHomeUsageKwh(lastRecord.getTotalHomeUsageKwh());
        history.setEndGridImportKwh(lastRecord.getTotalGridImportKwh());
        history.setEndGridExportKwh(lastRecord.getTotalGridExportKwh());

        history.setAvgSoC(socStats.getAverage());
        history.setMinSoC(socStats.getMin());
        history.setMaxSoC(socStats.getMax());

        // 5. Save
        repository.save(history);

        log.info(
                "saveAggregate: aggregated and saved {} records for meterId = {}",
                bucket.size(),
                meterId);
        return history;
    }

}

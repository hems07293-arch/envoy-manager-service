package com.project.hems.envoy_manager_service.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.domain.MeterHistory;
import com.project.hems.envoy_manager_service.model.BatteryControl;
import com.project.hems.envoy_manager_service.model.GridControl;
import com.project.hems.envoy_manager_service.model.GridControl.GridControlBuilder;
import com.project.hems.envoy_manager_service.model.BatteryControl.BatteryControlBuilder;
import com.project.hems.envoy_manager_service.model.SiteControlCommand;
import com.project.hems.envoy_manager_service.model.SiteControlCommand.SiteControlCommandBuilder;
import com.project.hems.envoy_manager_service.model.dispatch.DispatchEvent;
import com.project.hems.envoy_manager_service.model.dispatch.DispatchEventType;
import com.project.hems.envoy_manager_service.model.simulator.BatteryMode;
import com.project.hems.envoy_manager_service.repository.MeterHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandTranslatorService {

    private final MeterHistoryRepository meterHistoryRepository;

    public SiteControlCommand translateDispatchEvent(DispatchEvent dispatchEvent) {

        // 1. Basic metadata mapping
        SiteControlCommandBuilder commandBuilder = SiteControlCommand.builder();
        commandBuilder.dispatchId(dispatchEvent.getDispatchId());
        commandBuilder.siteId(dispatchEvent.getSiteId());

        Optional<MeterHistory> meterHistoryOptional = meterHistoryRepository
                .findTopBySiteIdOrderByTimestampDesc(dispatchEvent.getSiteId());

        meterHistoryOptional.ifPresentOrElse(meterHistory -> {
            commandBuilder.meterId(meterHistory.getMeterId());
        }, () -> {
            log.error("translateDispatchEvent: required meter history not found in db");
        });

        // 2. Calculate Expiry (validUntil = Now + durationSec)
        commandBuilder.timestamp(Instant.now());
        commandBuilder.validUntil(Instant.now().plusSeconds(dispatchEvent.getDurationSec()));

        // 3. Map Priorities directly
        commandBuilder.energyPriority(dispatchEvent.getEnergyPriority());
        commandBuilder.reason(dispatchEvent.getReason());

        // 4. Initialize Default Controls (Safety First!)
        BatteryControlBuilder batteryControlBuilder = BatteryControl.builder();
        GridControlBuilder gridControlBuilder = GridControl.builder();

        // set default values
        batteryControlBuilder.minSocPercent(20.0);
        batteryControlBuilder.maxSocPercent(100.0);
        gridControlBuilder.allowImport(true);

        // 5. THE LOGIC SWITCH (The "Brain" of the translation)
        switch (dispatchEvent.getEventType()) {

            case DispatchEventType.EXPORT_POWER:
                // Logic: Force battery to dump energy, allow grid to take it
                batteryControlBuilder.mode(BatteryMode.FORCE_DISCHARGE);

                // Limit the discharge to exactly what was requested
                batteryControlBuilder.maxDischargeW(dispatchEvent.getPowerReqW());
                batteryControlBuilder.maxChargeW(0l); // Don't charge while exporting!

                gridControlBuilder.allowExport(true);
                gridControlBuilder.maxExportW(dispatchEvent.getPowerReqW() * 1.1);
                gridControlBuilder.maxImportW(10000.00); // Standard limit
                break;

            case DispatchEventType.IMPORT_POWER: // e.g. Charge before a storm
                // Logic: Force charge, block exporting
                batteryControlBuilder.mode(BatteryMode.FORCE_CHARGE);
                batteryControlBuilder.maxChargeW(dispatchEvent.getPowerReqW());

                gridControlBuilder.allowExport(false); // Don't sell energy now
                gridControlBuilder.maxImportW(dispatchEvent.getPowerReqW() * 1.1);
                break;

            case DispatchEventType.PEAK_SAVING: // Reduce grid usage during expensive hours
                // Logic: Use battery to cover home load, but don't force dump
                batteryControlBuilder.mode(BatteryMode.AUTO); // Or "SELF_CONSUMPTION"

                // Usually Peak Shaving means "Don't buy from grid if possible"
                // But here we might just cap the Grid Import
                gridControlBuilder.allowExport(true);
                gridControlBuilder.maxImportW(100.0); // Try to stay near 0 import
                break;

            default:
                // Fallback to "Business as Usual"
                batteryControlBuilder.mode(BatteryMode.AUTO);
                gridControlBuilder.allowExport(true);
                gridControlBuilder.maxExportW(5000.00);
                gridControlBuilder.maxImportW(5000.00);
                break;
        }

        batteryControlBuilder.targetPowerW(dispatchEvent.getPowerReqW());
        commandBuilder.batteryControl(batteryControlBuilder.build());
        commandBuilder.gridControl(gridControlBuilder.build());

        return commandBuilder.build();
    }
}

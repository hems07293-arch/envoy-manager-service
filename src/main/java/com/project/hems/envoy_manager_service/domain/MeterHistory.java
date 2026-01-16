package com.project.hems.envoy_manager_service.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "meter_history")
public class MeterHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long meterId;
    @Column(nullable = false)
    private Long siteId;
    private LocalDateTime timestamp; // The start time of this 15-min bucket

    // --- AVERAGES (For Power Graphs) ---
    // We store the AVERAGE Watts over the 15 minutes
    private Double avgSolarW;
    private Double avgConsumptionW;
    private Double avgGridW;
    private Double avgBatteryW;

    // --- ACCUMULATORS (For Billing/Bar Charts) ---
    // We store the MAX (Last) kWh value in this window.
    // Frontend calculates usage by doing: (Row B - Row A)
    private Double endSolarKwh;
    private Double endGridImportKwh;
    private Double endGridExportKwh;
    private Double endHomeUsageKwh;

    // --- BATTERY HEALTH ---
    private Double avgSoC; // Average State of Charge %
    private Double minSoC; // Lowest point in this window
    private Double maxSoC; // Highest point in this window
}

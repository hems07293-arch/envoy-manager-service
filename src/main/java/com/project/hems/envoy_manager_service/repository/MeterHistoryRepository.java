package com.project.hems.envoy_manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.hems.envoy_manager_service.domain.MeterHistory;
import java.util.Optional;

public interface MeterHistoryRepository extends JpaRepository<MeterHistory, Long> {

    Optional<MeterHistory> findTopBySiteIdOrderByTimestampDesc(Long siteId);
}

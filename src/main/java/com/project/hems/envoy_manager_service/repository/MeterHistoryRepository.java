package com.project.hems.envoy_manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.hems.envoy_manager_service.domain.MeterHistory;

public interface MeterHistoryRepository extends JpaRepository<MeterHistory, Long> {

}

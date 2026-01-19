package com.project.hems.envoy_manager_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class EnvoyManagerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnvoyManagerServiceApplication.class, args);
	}

}

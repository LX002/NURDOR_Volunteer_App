package com.nurdor_project.events_log_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
public class EventsLogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventsLogServiceApplication.class, args);
	}

}

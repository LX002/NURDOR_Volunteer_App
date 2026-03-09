package com.nurdor_project.donations_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class DonationsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonationsServiceApplication.class, args);
	}

}

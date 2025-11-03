package ru.practicum.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"ru.practicum.feign", "ru.practicum.client"})
@ConfigurationPropertiesScan
public class EventServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(EventServiceApplication.class, args);
	}

}

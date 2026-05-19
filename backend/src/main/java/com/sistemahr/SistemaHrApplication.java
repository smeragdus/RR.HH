package com.sistemahr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SistemaHrApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaHrApplication.class, args);
	}

}

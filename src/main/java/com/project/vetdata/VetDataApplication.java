package com.project.vetdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.project.vetdata.model")
@EnableJpaRepositories(basePackages = "com.project.vetdata.repository")
public class VetDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(VetDataApplication.class, args);
	}

}

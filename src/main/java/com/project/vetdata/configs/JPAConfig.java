package com.project.vetdata.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.project.vetdata.model")
@EnableJpaRepositories(basePackages = "com.project.vetdata.repository")
@Configuration
public class JPAConfig {

}
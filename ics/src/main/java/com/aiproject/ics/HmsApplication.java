package com.aiproject.ics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@EnableJpaRepositories(basePackages = "com.aiproject.ics.repository.jpa")
@EnableJdbcRepositories(basePackages = "com.aiproject.ics.repository.jdbc")
public class HmsApplication {

	public static void main(String[] args) {
		System.out.println("ENV TEST DB USERNAME = " + System.getenv("DATASOURCE_USERNAME"));
		SpringApplication.run(HmsApplication.class, args);
	}


}

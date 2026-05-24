package com.surya.empsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmpsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmpsyncApplication.class, args);
	}

}

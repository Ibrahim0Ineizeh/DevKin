package com.example.devkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.devkin")
public class  DevkinApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevkinApplication.class, args);
	}

}

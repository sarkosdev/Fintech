package com.example.fintech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Micro2Application {

	private final static Logger logger = LoggerFactory.getLogger(Micro2Application.class);

	private final static String appName = "Simple API & Wallet";

	public static void main(String[] args) {
		SpringApplication.run(Micro2Application.class, args);
		logger.debug("Application :: " + appName + " -> STARTED WITH SUCCESS");
	}

}

package com.playsafeholding.assessment.roulette;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RouletteApplication implements CommandLineRunner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RouletteApplication.class);
	
	public static void main(String[] args) {
		LOGGER.info("STARTING THE CONSOLE ROULETTE APPLICATION");
		SpringApplication.run(RouletteApplication.class, args);
		LOGGER.info("CONSOLE ROULETTE APPLICATION FINISHED");
	}
	
	@Override
    public void run(String... args) throws Exception {
		new Round().playGame();
	}
}

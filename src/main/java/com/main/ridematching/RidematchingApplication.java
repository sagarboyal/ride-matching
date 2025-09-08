package com.main.ridematching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class RidematchingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RidematchingApplication.class, args);
	}

}

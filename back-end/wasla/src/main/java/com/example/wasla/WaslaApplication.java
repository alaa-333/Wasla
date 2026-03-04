package com.example.wasla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WaslaApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaslaApplication.class, args);
	}

}

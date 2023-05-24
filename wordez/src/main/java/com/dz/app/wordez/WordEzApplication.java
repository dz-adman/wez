package com.dz.app.wordez;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableR2dbcRepositories
public class WordEzApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordEzApplication.class, args);
	}

}

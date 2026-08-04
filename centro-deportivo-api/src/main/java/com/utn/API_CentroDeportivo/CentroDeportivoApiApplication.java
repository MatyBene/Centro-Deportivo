package com.utn.API_CentroDeportivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CentroDeportivoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CentroDeportivoApiApplication.class, args);
	}

}

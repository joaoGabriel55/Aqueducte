package br.imd.aqueducte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class AqueducteApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AqueducteApplication.class, args);
	}

}

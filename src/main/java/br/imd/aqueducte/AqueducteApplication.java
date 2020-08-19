package br.imd.aqueducte;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Log4j2
@SpringBootApplication
public class AqueducteApplication extends SpringBootServletInitializer {

    @Value("${application.environment}")
    private static String environment;

    public static void main(String[] args) {
        log.info(environment);
        SpringApplication.run(AqueducteApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    private static final Class<AqueducteApplication> applicationClass = AqueducteApplication.class;

}


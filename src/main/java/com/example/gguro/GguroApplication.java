package com.example.gguro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GguroApplication {

    public static void main(String[] args) {
        SpringApplication.run(GguroApplication.class, args);
    }

}

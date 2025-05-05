package com.example.donation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoacaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoacaoApplication.class, args);
    }
}

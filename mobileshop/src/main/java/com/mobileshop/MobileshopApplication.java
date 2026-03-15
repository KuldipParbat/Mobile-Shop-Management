package com.mobileshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MobileshopApplication {
    public static void main(String[] args) {
        SpringApplication.run(MobileshopApplication.class, args);
    }
}
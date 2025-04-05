package com.ticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TicketingApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TicketingApplication.class, args);
    }
}

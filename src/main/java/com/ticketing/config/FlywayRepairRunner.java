package com.ticketing.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlywayRepairRunner {

    private final Flyway flyway;

    /**
     * This bean will run Flyway.repair() when the "repair-flyway" profile is active.
     * To use this, run your app with: 
     * --spring.profiles.active=repair-flyway
     */
    @Bean
    @Profile("repair-flyway")
    public CommandLineRunner repairFlyway() {
        return args -> {
            log.info("Repairing Flyway migrations...");
            flyway.repair();
            log.info("Flyway repair completed");
        };
    }
}

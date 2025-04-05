package com.ticketing.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer {

    @Value("${spring.datasource.username:postgres}")
    private String dbUsername;

    @Value("${spring.datasource.password:postgres}")
    private String dbPassword;

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/ticketing}")
    private String dbUrl;

    @PostConstruct
    public void initializeDatabase() {
        try {
            log.info("Checking if database 'ticketing' exists...");
            
            // Connect to PostgreSQL server
            Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres", 
                dbUsername, 
                dbPassword
            );

            // Check if database exists
            try (Statement statement = connection.createStatement()) {
                // Try to create the database if it doesn't exist
                statement.executeUpdate("CREATE DATABASE ticketing WITH OWNER = " + dbUsername);
                log.info("Database 'ticketing' created successfully.");
            } catch (Exception e) {
                // Database might already exist
                log.info("Database 'ticketing' already exists or could not be created: {}", e.getMessage());
            }

            connection.close();
            log.info("Database initialization complete");
        } catch (Exception e) {
            log.error("Failed to initialize database: {}", e.getMessage(), e);
        }
    }
}

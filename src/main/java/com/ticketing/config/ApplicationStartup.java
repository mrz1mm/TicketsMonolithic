package com.ticketing.config;

import com.ticketing.model.Role;
import com.ticketing.model.User;
import com.ticketing.repository.RoleRepository;
import com.ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void ensureAdminUserExists() {
        String adminUsername = "admin";
        Optional<User> existingAdmin = userRepository.findByUsername(adminUsername);

        if (existingAdmin.isEmpty()) {
            log.info("Admin user not found. Creating default admin user...");

            // Check if admin role exists
            Optional<Role> adminRole = roleRepository.findByName("ADMIN");
            Role role;

            if (adminRole.isEmpty()) {
                log.info("Admin role not found. Creating default admin role...");
                role = new Role();
                role.setName("ADMIN");
                role.setDescription("Administrator with full access");
                roleRepository.save(role);
            } else {
                role = adminRole.get();
            }

            // Create admin user
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .firstName("Admin")
                    .lastName("User")
                    .active(true)
                    .roles(new HashSet<>())
                    .build();

            admin.getRoles().add(role);
            userRepository.save(admin);
            log.info("Default admin user created successfully");
        } else {
            log.info("Admin user already exists");
        }
    }
}

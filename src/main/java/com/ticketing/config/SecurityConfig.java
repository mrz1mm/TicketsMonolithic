package com.ticketing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Value("${app.auth.remember-me-key:uniqueAndSecretKey}")
    private String rememberMeKey;

    @Value("${app.auth.remember-me-validity-seconds:604800}")
    private int rememberMeValiditySeconds;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        // Use consistent strength parameter (10) for BCrypt
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> 
                authorize
                    .requestMatchers("/", "/welcome").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**", "/error").permitAll()
                    .requestMatchers("/register", "/login").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .formLogin(form -> 
                form
                    .loginPage("/login")
                    .defaultSuccessUrl("/dashboard")
                    .permitAll()
            )
            .logout(logout -> 
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            )
            .rememberMe(remember -> 
                remember
                    .key(rememberMeKey)
                    .tokenValiditySeconds(rememberMeValiditySeconds)
                    .userDetailsService(userDetailsService)
            );
        
        return http.build();
    }
}

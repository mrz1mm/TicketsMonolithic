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
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

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
            // Abilita la protezione CSRF (è importante per un'applicazione web con form)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // Ignora per le API se necessario
            .authorizeHttpRequests(authorize -> 
                authorize
                    .requestMatchers("/", "/welcome").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**", "/error").permitAll()
                    .requestMatchers("/register", "/login").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/dashboard").hasAnyRole("USER", "ADMIN", "SUPPORT")
                    .anyRequest().authenticated()
            )
            .formLogin(form -> 
                form
                    .loginPage("/login")
                    .defaultSuccessUrl("/dashboard", true)
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
            )
            // Aggiungi header di sicurezza comuni
            .headers(headers -> 
                headers
                    .contentSecurityPolicy(csp -> 
                        csp.policyDirectives("default-src 'self'; script-src 'self' https://cdn.jsdelivr.net; style-src 'self' https://cdn.jsdelivr.net; font-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; img-src 'self' data:;")
                    )
                    .frameOptions(frame -> frame.sameOrigin())
                    .xssProtection(xss -> xss.enable())
                    .contentTypeOptions(contentType -> contentType.disable()) // Enable Content-Type Options
                    .referrerPolicy(referrer -> 
                        referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                    )
                    .permissionsPolicy(permissions -> 
                        permissions.policy("camera=(), microphone=(), geolocation=()")
                    )
            );
        
        return http.build();
    }
}

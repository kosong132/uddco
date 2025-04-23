package com.uddco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/**"
                        // "/auth/register",
                        // "/auth/login",
                        // "/auth/request-reset-password",
                        // "/auth/request-reset-password/mobile",
                        // "/auth/reset-password",
                        // "/auth/reset-password/mobile",
                        // "/request-reset-password-json",
                        // // Product Management - Public for now
                        // "/products/add",
                        // "/products/upload-image",
                        // "/products/all",
                        // "/products/{productId}",
                        // "/products/update/{productId}",
                        // "/products/delete/{productId}",
                        // "/uploads/**"
                ).permitAll() // Allow public access to these endpoints
                .anyRequest().authenticated() // All other requests require authentication
                )
                .httpBasic(withDefaults()); // Use HTTP Basic Authentication (for simplicity)
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password hashing
    }
}

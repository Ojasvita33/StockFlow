package com.stockflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(new JwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Allow the dashboard page and its static assets to load freely
                        // — script.js will redirect to authService if no token is found
                        .requestMatchers("/", "/index.html", "/*.js", "/*.css").permitAll()
                        // All API calls must carry a valid JWT
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated());

        return http.build();
    }
}

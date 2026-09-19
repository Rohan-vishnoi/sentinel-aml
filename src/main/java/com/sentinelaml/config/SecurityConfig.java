package com.sentinelaml.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final SentinelProperties properties;

    public SecurityConfig(SentinelProperties properties) {
        this.properties = properties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername(properties.security().adminUsername())
                        .password(encoder.encode(properties.security().adminPassword()))
                        .roles("ADMIN", "ANALYST")
                        .build(),
                User.withUsername(properties.security().analystUsername())
                        .password(encoder.encode(properties.security().analystPassword()))
                        .roles("ANALYST")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/swagger-ui/**", "/api-docs/**", "/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/dashboard", "/dashboard/**").hasAnyRole("ANALYST", "ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/alerts/**", "/api/v1/cases/**").hasAnyRole("ANALYST", "ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }
}

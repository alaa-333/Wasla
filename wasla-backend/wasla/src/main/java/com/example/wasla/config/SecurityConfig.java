package com.example.wasla.config;

import com.example.wasla.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration aligned with documented endpoint permissions.
 * JWT-based stateless authentication with role-based access control.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── Public ──
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**",
                                "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // ── Admin only ──
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")

                        // ── Client only ──
                        .requestMatchers(HttpMethod.POST, "/api/v1/jobs").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/jobs/*/bids/*/accept")
                            .hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/v1/jobs/*/rating")
                            .hasAuthority("CLIENT")
                        .requestMatchers("/api/v1/clients/**").hasAuthority("CLIENT")

                        // ── Driver only ──
                        .requestMatchers(HttpMethod.GET, "/api/v1/jobs/nearby")
                            .hasAuthority("DRIVER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/jobs/*/bids")
                            .hasAuthority("DRIVER")
                        .requestMatchers("/api/v1/drivers/me/**").hasAuthority("DRIVER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/drivers/me").hasAuthority("DRIVER")

                        // ── Any authenticated ──
                        .requestMatchers(HttpMethod.GET, "/api/v1/drivers/*").authenticated()

                        .anyRequest().authenticated())

                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
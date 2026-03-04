package com.example.wasla.config;


import com.example.wasla.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // تسمح لك باستخدام @PreAuthorize لتحديد الأدوار (Driver/Client)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // تعطيل CSRF لأننا نستخدم JWT (Stateless)
                .cors(Customizer.withDefaults()) // تفعيل الـ CORS للسماح لـ Flutter بالاتصال بالسيرفر
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() // السماح بالدخول لشاشات تسجيل الدخول دون Token
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // فتح التوثيق للجنة التحكيم
                        .requestMatchers("/api/v1/driver/**").hasAuthority("DRIVER") // مسارات خاصة بالسائق فقط
                        .requestMatchers("/api/v1/client/**").hasAuthority("CLIENT") // مسارات خاصة بالعميل فقط
                        .anyRequest().authenticated() // أي طلب آخر يتطلب Token
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // إخبار Spring بعدم إنشاء Session
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // وضع فلتر الـ JWT قبل فلتر الأمن الافتراضي

        return http.build();
    }

    // إعدادات الـ CORS مهمة جداً لكي لا يرفض السيرفر طلبات تطبيق الـ Flutter
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // في الإنتاج نحدد روابط معينة، في التخرج نفتحها للسهولة
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
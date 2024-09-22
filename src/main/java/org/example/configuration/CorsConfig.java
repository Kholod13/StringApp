package org.example.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Вказуємо маршрути, на які діє CORS
                        .allowedOrigins("*") // Дозволені домени
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Дозволені HTTP методи
                        .allowedHeaders("*") // Дозволені заголовки
                        .allowCredentials(false); // Дозвіл на відправку cookies
            }
        };
    }
}
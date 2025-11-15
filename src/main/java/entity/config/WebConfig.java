package entity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS para permitir peticiones desde el frontend Angular
 * Esto asegura que todas las peticiones HTTP (incluyendo POST/PUT con JSON) sean permitidas
 * 
 * IMPORTANTE: Usamos allowedOriginPatterns en lugar de allowedOrigins cuando
 * allowCredentials está en true, para evitar el error:
 * "When allowCredentials is true, allowedOrigins cannot contain the special value *"
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

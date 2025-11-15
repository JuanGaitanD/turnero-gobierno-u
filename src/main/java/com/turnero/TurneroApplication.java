package com.turnero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuración principal. Se amplía el escaneo de componentes para incluir el paquete "entity" donde
 * se ubicaron controladores, servicios y repositorios. Esto soluciona el 404 al acceder a /api/turnos.
 */
@SpringBootApplication(scanBasePackages = {"com.turnero", "entity"})
@EntityScan(basePackages = {"entity"})
@EnableJpaRepositories(basePackages = {"entity.repository"})
@ComponentScan(basePackages = {"com.turnero", "entity"})
public class TurneroApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurneroApplication.class, args);
	}

}

package com.biblioteca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API del Sistema Integrado de Gestión Bibliotecaria")
                        .version("1.0.0")
                        .description("Documentación de los endpoints para el control de libros, ejemplares y préstamos.")
                        .contact(new Contact()
                                .name("Bernardo Manuel Vargas Cruz")
                                .email("vargasbernardo555@gmail.com"))
                        .contact(new Contact()
                                .name("José De Jesús Aguilar Herrera")
                                .email("correoJose@gmail.com")));
    }
}
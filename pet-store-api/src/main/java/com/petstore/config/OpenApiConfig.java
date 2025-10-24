package com.petstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Configuration class for Swagger / OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

     /**
     * Configures the OpenAPI (Swagger) definition for the application.
     *
     * @return a fully configured {@link OpenAPI} bean used by SpringDoc to generate
     *         Swagger UI and API documentation.
     */
    @Bean
    public OpenAPI petStoreOpenAPI() {

        final String securitySchemeName = "bearerAuth";
        var components = new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );


        Contact contact = new Contact();
        contact.setEmail("admin@petstore.com");
        contact.setName("Pawfect Store Team");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Pawfect Store API")
                .version("1.0.0")
                .contact(contact)
                .description("This API exposes endpoints to manage pets in a pet store.")
                .license(license);

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(components)
                .info(info);
    }
}
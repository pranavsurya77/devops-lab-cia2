package com.cia2.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Swagger UI is served at /swagger-ui.html, the raw OpenAPI spec at /v3/api-docs. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI libraryOpenApi(@Value("${app.version}") String version) {
        return new OpenAPI().info(new Info()
                .title("Library Book Manager API")
                .version(version)
                .description("Add, search, borrow and return library books."));
    }
}

package com.library.bible.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
	
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Bible Library API")
                .version("v1.0")
                .description("Bible Library API 명세서");

        return new OpenAPI()
                .components(new Components())
                .info(info)
                .servers(List.of(new Server().url("http://localhost:8080")));
    }
}
package com.thanhhandsome.pythonmaster.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server; // Import thêm class Server
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI pythonMasterOpenAPI() {
        // Server tương đối: Swagger gọi đúng origin đang mở trang (https trên Railway,
        // localhost khi chạy máy) nên không dính mismatch http/https hay CORS.
        Server currentServer = new Server()
                .url("/")
                .description("Current server");

        return new OpenAPI()
                .servers(List.of(currentServer))
                .info(new Info()
                        .title("Python Master API")
                        .description("REST API quản lý thí sinh, đối tác, dashboard và xác thực JWT.")
                        .version("v1")
                        .contact(new Contact().name("Python Master").email("admin@pythonmaster.vn")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Dán accessToken sau khi gọi POST /api/v1/auth/login"))
                );
    }
}
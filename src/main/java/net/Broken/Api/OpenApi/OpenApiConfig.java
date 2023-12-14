package net.Broken.Api.OpenApi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import net.Broken.VersionLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private final VersionLoader versionLoader;

    public OpenApiConfig(VersionLoader version) {
        this.versionLoader = version;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "JWT";
        return new OpenAPI().addSecurityItem(
                new SecurityRequirement().addList(securitySchemeName)).components(
                        new Components().addSecuritySchemes(
                                securitySchemeName,
                                new SecurityScheme().name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addServersItem(new Server().url("/").description("Default"))
                .info(new Info().title("ClaptrapBot API").version(versionLoader.getVersion()));
    }
}
package ztt.fixedassetmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger. v3.oas.models.info. Contact;
import io. swagger.v3. oas.models. info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context. annotation.Bean;
import org.springframework. context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                . info(new Info()
                        .title("固定资产管理系统 API")
                        . version("1.0. 0")
                        .description("学校固定资产管理系统后端API接口文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        . addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme. Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
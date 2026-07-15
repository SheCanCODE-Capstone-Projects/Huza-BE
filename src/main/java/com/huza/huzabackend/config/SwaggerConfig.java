package huza.huzabackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Huza Authentication Service API")
                        .version("1.0.0")
                        .description("""
                                **Huza Authentication Service**
                                
                                This API provides comprehensive user management and authentication features.
                                
                                ## Features
                                - ✅ User Registration
                                - ✅ Email Verification with OTP
                                - ✅ Profile Management
                                - ✅ User Status Management
                                - ✅ Role-Based Access Control
                                - ✅ JWT Authentication
                                - ✅ OAuth2 Login
                                - ✅ Password Reset with OTP
                                """)
                        .contact(new Contact()
                                .name("Huza Team")
                                .email("support@huza.com")
                                .url("https://huza.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.huza.com")
                                .description("Production Server")
                ));
    }
}
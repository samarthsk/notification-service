package com.banking.notification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI notificationServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .description("Banking Notification Service for sending email and SMS notifications")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Banking Team")
                                .email("support@banking.com")));
    }
}

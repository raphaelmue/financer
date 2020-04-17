package org.financer.server.application.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentationConfiguration {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .setGroup("public-api")
                .packagesToScan("org.financer.server.application.api")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("Financer API")
                .description("Financer is an application that helps to manage your personal expenses and revenues. It " +
                        "helps you to analyze how much you have spend on living, eating etc. Besides you can take a look " +
                        "at the temporal progression of your expenses as well as on your revenues.")
                .version(DocumentationConfiguration.class.getPackage().getImplementationVersion())
                .license(new License().name("BSD 3-Clause").url("https://github.com/raphaelmue/financer/blob/master/LICENSE"))
                .contact(new Contact().name("Raphael Müßeler").email("raphael@muesseler.de").url("https://raphael-muesseler.de"))
        );
    }
}

package org.financer.server.application.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentationConfiguration {

    @Value("${application.version}")
    private String version;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .setGroup("public-api")
                .packagesToScan("org.financer.server.application.api")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Financer API")
                        .version(version)
                        .description("Financer is an application that helps to manage your personal expenses and revenues. It " +
                                "helps you to analyze how much you have spend on living, eating etc. Besides you can take a look " +
                                "at the temporal progression of your expenses as well as on your revenues.")
                        .license(new License().name("BSD 3-Clause").url("https://github.com/raphaelmue/financer/blob/master/LICENSE"))
                        .contact(new Contact().name("Raphael Müßeler").email("raphael@muesseler.de").url("https://raphael-muesseler.de"))
                ).addServersItem(new Server().url("https://api.financer-project.org" + contextPath).description("Default Server for production."));
    }
}

package org.financer.server.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
        exclude = {HibernateJpaAutoConfiguration.class},
        scanBasePackages = {
                "org.financer.server.application.service",
                "org.financer.server.application.api",
                "org.financer.server.application.configuration",
                "org.financer.server.application.model",
                "org.financer.server.domain.service",
                "org.financer.server.domain.repository"
        })
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableJpaRepositories(basePackages = "org.financer.server.domain.repository")
public class FinancerServer {

    public static void main(String[] args) {
        SpringApplication.run(FinancerServer.class, args);
    }
}

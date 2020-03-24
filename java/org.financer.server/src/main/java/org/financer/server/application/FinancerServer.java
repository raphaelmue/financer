package org.financer.server.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        exclude = {HibernateJpaAutoConfiguration.class},
        scanBasePackages = {
                "org.financer.server.application.service",
                "org.financer.server.application.api",
                "org.financer.server.application.configuration"
        })
@EnableJpaRepositories(basePackages = "org.financer.server.domain.repository")
public class FinancerServer {

    public static void main(String[] args) {
        SpringApplication.run(FinancerServer.class, args);
    }
}

package org.financer.server.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(
        exclude = {HibernateJpaAutoConfiguration.class},
        scanBasePackages = {"org.financer.server.service", "org.financer.server.api", "org.financer.server.configuration"})
public class FinancerServer {

    public static void main(String[] args) {
        SpringApplication.run(FinancerServer.class, args);
    }
}

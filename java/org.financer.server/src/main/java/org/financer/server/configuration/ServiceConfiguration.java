package org.financer.server.configuration;

import org.financer.server.service.FinancerService;
import org.financer.server.service.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class ServiceConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ServiceConfiguration.class);

    @Bean
    public FinancerService financerService() {
        return new FinancerService();
    }

    @Bean
    public VerificationService verificationService() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/application.properties"));
            if (properties.getProperty("financer.server.smtp").equals(Boolean.toString(true))) {
                return new VerificationService(
                        properties.getProperty("financer.server.smtp.host"),
                        Integer.parseInt(properties.getProperty("financer.server.smtp.port")),
                        properties.getProperty("financer.server.smtp.email"),
                        properties.getProperty("financer.server.smtp.password")
                );
            }
        } catch (IOException e) {
            logger.error("Failed to load hibernate properties for instantiating data source.", e);
        }
        return new VerificationService("", -1, "", "");
    }
}

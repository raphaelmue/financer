package org.financer.server.application.configuration;

import org.financer.server.application.service.FinancerService;
import org.financer.server.application.service.VerificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class ServiceConfiguration {

    @Bean
    public FinancerService financerService() {
        return new FinancerService();
    }

    @Bean
    public VerificationService verificationService() {
        return new VerificationService();
    }
}

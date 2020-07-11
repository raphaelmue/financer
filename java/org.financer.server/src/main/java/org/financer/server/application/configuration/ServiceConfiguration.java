package org.financer.server.application.configuration;

import org.financer.server.application.service.FinancerService;
import org.financer.server.application.service.VerificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@Configuration
@EnableTransactionManagement
public class ServiceConfiguration {

    @Bean
    public FinancerService financerService() {
        return new FinancerService();
    }

    @Bean
    public VerificationService verificationService() throws IOException {
        return new VerificationService();
    }
}

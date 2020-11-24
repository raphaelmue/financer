package org.financer.server.utils;

import org.financer.server.application.FinancerServer;
import org.financer.server.application.configuration.MigrationConfiguration;
import org.financer.server.application.configuration.PersistenceConfiguration;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.application.service.VerificationService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = {FinancerServer.class, PersistenceConfiguration.class, MigrationConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ServiceTest extends SpringTest {

    @MockBean
    protected VerificationService verificationService;

    @MockBean
    protected AuthenticationService authenticationService;

    protected void mockAnotherUserAuthenticated() {
        when(authenticationService.getUserId()).thenReturn(-1L);
    }

}

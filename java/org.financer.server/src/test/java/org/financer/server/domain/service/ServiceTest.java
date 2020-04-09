package org.financer.server.domain.service;

import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.repository.*;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

public abstract class ServiceTest {

    @MockBean
    protected AuthenticationService authenticationService;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected TokenRepository tokenRepository;

    @MockBean
    protected VerificationTokenRepository verificationTokenRepository;

    @MockBean
    protected CategoryRepository categoryRepository;

    @MockBean
    protected VariableTransactionRepository variableTransactionRepository;

    @MockBean
    protected FixedTransactionRepository fixedTransactionRepository;

    @MockBean
    protected AttachmentRepository attachmentRepository;

    protected void mockAnotherUserAuthenticated() {
        when(authenticationService.getUserId()).thenReturn(-1L);
    }
}

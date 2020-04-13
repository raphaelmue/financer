package org.financer.server;

import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.repository.*;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

public abstract class SpringTest {

    protected static final String CONTEXT_PATH = "/api";

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
    protected FixedTransactionAmountRepository fixedTransactionAmountRepository;

    @MockBean
    protected AttachmentRepository attachmentRepository;

    @MockBean
    protected ProductRepository productRepository;

    protected void mockAnotherUserAuthenticated() {
        when(authenticationService.getUserId()).thenReturn(-1L);
    }
}

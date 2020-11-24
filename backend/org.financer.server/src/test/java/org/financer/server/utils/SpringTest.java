package org.financer.server.utils;

import org.financer.server.domain.repository.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = "test")
public abstract class SpringTest extends MockData {

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected RoleRepository roleRepository;

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

}

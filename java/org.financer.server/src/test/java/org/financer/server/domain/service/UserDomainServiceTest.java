package org.financer.server.domain.service;

import org.financer.server.application.FinancerServer;
import org.financer.server.domain.model.user.TokenEntity;
import org.financer.server.domain.model.user.UserEntity;
import org.financer.server.domain.repository.TokenRepository;
import org.financer.server.domain.repository.UserRepository;
import org.financer.server.domain.repository.VerificationTokenRepository;
import org.financer.shared.domain.model.value.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FinancerServer.class, UserDomainService.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserDomainServiceTest {

    @Autowired
    private UserDomainService userDomainService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private VerificationTokenRepository verificationTokenRepository;

    private static final UserEntity user = new UserEntity()
            .setId(1)
            .setEmail(new Email("test@test.com"))
            .setName(new Name("Test", "User"))
            .setPassword(new HashedPassword("password"));

    @BeforeEach
    public void setUp() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(i -> i.getArguments()[0]);;
    }

    @Test
    public void checkCredentials() {
        Optional<UserEntity> userToAssert = userDomainService.checkCredentials("test@test.com", "password",
                new IPAddress("192.168.0.1"), new OperatingSystem(OperatingSystem.Values.LINUX));

        assertThat(userToAssert).isPresent();
        assertThat(userToAssert.get().getTokens()).isNotEmpty().first()
                .matches(tokenEntity -> tokenEntity.getExpireDate().isValid())
                .matches(tokenEntity -> !tokenEntity.getToken().getToken().isEmpty());
    }

    @Test
    public void checkUsersToken() {
    }

    @Test
    public void registerUser() {
    }

    @Test
    public void verifyUser() {
    }
}
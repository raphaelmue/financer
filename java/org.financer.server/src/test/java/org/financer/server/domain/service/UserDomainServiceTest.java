package org.financer.server.domain.service;

import org.financer.server.application.FinancerServer;
import org.financer.server.domain.model.user.TokenEntity;
import org.financer.server.domain.model.user.UserEntity;
import org.financer.server.domain.model.user.VerificationTokenEntity;
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

import java.util.Collections;
import java.util.HashSet;
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

    private static final TokenString tokenString =
            new TokenString("Z6XCS3tyyBlhPfsv7rMxLgfdyEOlUkv0CWSdbFYHCNX2wLlwpH97lJNP69Bny3XZ");

    private static final TokenEntity token = new TokenEntity()
            .setId(1)
            .setExpireDate(new ExpireDate())
            .setIpAddress(new IPAddress("192.168.0.1"))
            .setToken(tokenString)
            .setOperatingSystem(new OperatingSystem(OperatingSystem.Values.LINUX));

    private static final VerificationTokenEntity verificationToken = new VerificationTokenEntity()
            .setId(1)
            .setExpireDate(new ExpireDate())
            .setToken(tokenString);

    private static final UserEntity user = new UserEntity()
            .setId(1)
            .setEmail(new Email("test@test.com"))
            .setName(new Name("Test", "User"))
            .setPassword(new HashedPassword("password"))
            .setTokens(new HashSet<>(Collections.singletonList(token)))
            .setVerificationToken(verificationToken);

    @BeforeEach
    public void setUp() {
        token.setUser(user);
        verificationToken.setUser(user);

        when(userRepository.save(any(UserEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        when(tokenRepository.getTokenByToken(tokenString)).thenReturn(Optional.of(token));
        when(verificationTokenRepository.save(any(VerificationTokenEntity.class))).thenAnswer(i -> i.getArguments()[0]);
        when(verificationTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(verificationToken));
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
        Optional<UserEntity> userToAssert = userDomainService.checkUsersToken(
                new TokenString("Z6XCS3tyyBlhPfsv7rMxLgfdyEOlUkv0CWSdbFYHCNX2wLlwpH97lJNP69Bny3XZ"));

        assertThat(userToAssert).isPresent();
        assertThat(userToAssert.get().getTokens()).isNotEmpty();
    }

    @Test
    public void registerUser() {
        UserEntity userToAssert = userDomainService.registerUser(user, new IPAddress("192.168.0.1"),
                new OperatingSystem(OperatingSystem.Values.LINUX));

        assertThat(userToAssert).isNotNull();
        assertThat(userToAssert.getTokens()).isNotEmpty();
        assertThat(userToAssert.getVerificationToken()).isNotNull();
        assertThat(userToAssert.isVerified()).isFalse();
    }

    @Test
    public void verifyUser() {
        Optional<UserEntity> userToAssert = userDomainService.verifyUser(tokenString);

        assertThat(userToAssert).isPresent();
        assertThat(userToAssert.get().isVerified()).isTrue();
    }
}
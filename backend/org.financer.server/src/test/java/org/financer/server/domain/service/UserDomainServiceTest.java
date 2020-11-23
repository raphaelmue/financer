package org.financer.server.domain.service;

import org.financer.server.SpringTest;
import org.financer.server.application.FinancerServer;
import org.financer.server.application.api.error.UnauthorizedOperationException;
import org.financer.server.application.api.error.UniqueEmailViolationException;
import org.financer.server.application.service.AdminConfigurationService;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.user.Token;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.model.user.VerificationToken;
import org.financer.shared.domain.model.value.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FinancerServer.class, AdminConfigurationService.class, UserDomainService.class, AuthenticationService.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserDomainServiceTest extends SpringTest {

    @MockBean
    private CategoryDomainService categoryDomainService;

    @MockBean
    private TransactionDomainService transactionDomainService;

    @Autowired
    private UserDomainService userDomainService;

    private User user;
    private Token token;
    private TokenString tokenString;


    @BeforeEach
    public void setUp() {
        user = user();
        token = token();
        tokenString = tokenString();
        VerificationToken verificationToken = verificationToken();

        token.setUser(user);
        verificationToken.setUser(user);

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(authenticationService.getUserId()).thenReturn(user.getId());
        doAnswer(i -> {
            if (user.getRoles().stream().filter(role -> role.getName().equals(i.getArgument(0))).findAny().isEmpty()) {
                throw new UnauthorizedOperationException(user.getId());
            }
            return null;
        }).when(authenticationService).throwIfUserHasNotRole(anyString());

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(user)));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(userRole()));
        when(tokenRepository.save(any(Token.class))).thenAnswer(i -> i.getArguments()[0]);
        when(tokenRepository.findById(token.getId())).thenReturn(Optional.of(token));
        when(tokenRepository.getTokenByToken(tokenString)).thenReturn(Optional.of(token));
        when(tokenRepository.getTokenByIPAddress(user.getId(), token.getIpAddress())).thenReturn(Optional.of(token));
        when(verificationTokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArguments()[0]);
        when(verificationTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(verificationToken));
    }

    @Test
    public void testCheckCredentials() {
        Optional<User> userToAssert = userDomainService.checkCredentials("test@test.com", "password",
                new IPAddress("192.168.0.1"), new OperatingSystem(OperatingSystem.Values.LINUX));

        assertThat(userToAssert).isPresent();
        assertThat(userToAssert.get().getActiveToken()).isNotNull()
                .matches(tokenEntity -> tokenEntity.getExpireDate().isValid())
                .matches(tokenEntity -> !tokenEntity.getToken().getToken().isEmpty());
    }

    @Test
    public void testCheckCredentialsInvalid() {
        assertThat(userDomainService.checkCredentials("test@test.com", "wrong",
                new IPAddress("192.168.0.1"), new OperatingSystem(OperatingSystem.Values.LINUX))).isEmpty();
    }

    @Test
    public void testCheckUsersToken() {
        token.setExpireDate(new ExpireDate());
        Optional<User> userToAssert = userDomainService.checkUsersToken(tokenString);

        assertThat(userToAssert).isPresent();
        assertThat(userToAssert.get().getTokens()).isNotEmpty();
    }

    @Test
    public void testCheckUsersTokenInvalid() {
        assertThat(userDomainService.checkUsersToken(new TokenString("wrongTokenString"))).isEmpty();

        token.setExpireDate(new ExpireDate(LocalDate.now().minusMonths(1)));
        assertThat(userDomainService.checkUsersToken(tokenString)).isEmpty();
    }

    @Test
    public void testRegisterUser() {
        User userToAssert = userDomainService.registerUser(new User()
                        .setId(2L)
                        .setEmail(new Email("test2@test.com"))
                        .setName(new Name("Test", "Test"))
                        .setPassword(new HashedPassword("password")),
                new IPAddress("192.168.0.1"), new OperatingSystem(OperatingSystem.Values.LINUX));

        assertThat(userToAssert).isNotNull();
        assertThat(userToAssert.getActiveToken()).isNotNull();
        assertThat(userToAssert.getTokens()).isNotEmpty();
        assertThat(userToAssert.getVerificationToken()).isNotNull();
        assertThat(userToAssert.isVerified()).isFalse();
    }

    @Test
    public void testRegisterUserUniqueEmailViolation() {
        assertThatExceptionOfType(UniqueEmailViolationException.class).isThrownBy(() ->
                userDomainService.registerUser(user, new IPAddress("192.168.0.1"), new OperatingSystem(OperatingSystem.Values.LINUX)));
    }

    @Test
    public void testVerifyUser() {
        Optional<User> userToAssert = userDomainService.verifyUser(tokenString);

        assertThat(userToAssert).isPresent();
        assertThat(userToAssert.get().isVerified()).isTrue();
    }

    @Test
    public void testVerifyUserWrongToken() {
        assertThat(userDomainService.verifyUser(new TokenString("wrongTokenString"))).isEmpty();
    }

    @Test
    public void testDeleteToken() {
        userDomainService.deleteToken(token.getId());
    }

    @Test
    public void testDeleteTokenUnauthorizedOperation() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(
                () -> userDomainService.deleteToken(token.getId()));
    }

    @Test
    public void testUpdatePassword() {
        User userToAssert = userDomainService.updatePassword(new HashedPassword("newPassword"));
        assertThat(userToAssert.getPassword().isEqualTo("newPassword")).isTrue();
    }

    @Test
    public void testUpdatePersonalInformation() {
        final Name name = new Name("Another", "Name");
        final BirthDate birthDate = new BirthDate(LocalDate.now().minusYears(20));
        final Gender gender = new Gender(Gender.Values.FEMALE);
        User userToAssert = userDomainService.updatePersonalInformation(name, birthDate, gender);

        assertThat(userToAssert.getName()).isEqualTo(name);
        assertThat(userToAssert.getBirthDate()).isEqualTo(birthDate);
        assertThat(userToAssert.getGender()).isEqualTo(gender);
    }

    @Test
    public void testUpdatePersonalInformationWithoutChanges() {
        User userToAssert = userDomainService.updatePersonalInformation(user.getName(), user.getBirthDate(), user.getGender());
        assertThat(userToAssert).isEqualToComparingFieldByField(user);
    }

    @Test
    public void testUpdatePasswordUnauthorizedOperation() {
        mockAnotherUserAuthenticated();
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(() ->
                userDomainService.updatePassword(new HashedPassword("newPassword")));
    }

    @Test
    public void testFetchUsers() {
        assertThat(userDomainService.fetchUsers(Pageable.unpaged()).getSize()).isEqualTo(1);
    }
}
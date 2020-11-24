package org.financer.server.application.service;

import org.financer.server.application.FinancerServer;
import org.financer.server.application.api.error.UnauthorizedOperationException;
import org.financer.server.application.configuration.MigrationConfiguration;
import org.financer.server.application.configuration.PersistenceConfiguration;
import org.financer.server.application.configuration.security.AuthenticationUser;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.service.UserDomainService;
import org.financer.server.utils.SpringTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FinancerServer.class, AuthenticationService.class, PersistenceConfiguration.class, MigrationConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationServiceTest extends SpringTest {

    @MockBean
    private UserDomainService userDomainService;

    @MockBean
    private AdminConfigurationService adminConfigurationService;

    @Autowired
    private AuthenticationService authenticationService;

    private User user;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        user = user();

        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(
                new UsernamePasswordAuthenticationToken(user, null, new AuthenticationUser(user).getAuthorities()));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetAuthenticatedUser() {
        assertThat(authenticationService.getAuthenticatedUser()).isNotNull();

        when(securityContext.getAuthentication()).thenReturn(null);
        assertThat(authenticationService.getAuthenticatedUser()).isNull();
    }

    @Test
    public void testThrowIfUserHasNotRole() {
        authenticationService.throwIfUserHasNotRole("ADMIN");
        user.setRoles(Set.of(userRole()));
        when(securityContext.getAuthentication()).thenReturn(
                new UsernamePasswordAuthenticationToken(user, null, new AuthenticationUser(user).getAuthorities()));
        assertThatExceptionOfType(UnauthorizedOperationException.class).isThrownBy(() -> authenticationService.throwIfUserHasNotRole("ADMIN"));
    }

    @Test
    public void testGetUserId() {
        assertThat(authenticationService.getUserId()).isEqualTo(1L);
    }
}
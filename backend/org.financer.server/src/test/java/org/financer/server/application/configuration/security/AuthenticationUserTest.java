package org.financer.server.application.configuration.security;

import org.financer.server.domain.model.user.User;
import org.financer.server.utils.MockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class AuthenticationUserTest extends MockData {

    private User user;
    private AuthenticationUser authenticationUser;

    @BeforeEach
    public void setUp() {
        user = user();
        authenticationUser = new AuthenticationUser(user);
    }

    @Test
    public void testGetAuthorities() {
        assertThat(authenticationUser.getAuthorities()).hasSize(2);
    }

    @Test
    public void testGetPassword() {
        assertThat(authenticationUser.getPassword()).isEqualTo(user.getPassword().getHashedPassword());
    }

    @Test
    public void testGetUsername() {
        assertThat(authenticationUser.getUsername()).isEqualTo(user().getEmail().getEmailAddress());
    }

    @Test
    public void testIsAccountNonExpired() {
        assertThat(authenticationUser.isAccountNonExpired()).isTrue();
    }

    @Test
    public void testIsAccountNonLocked() {
        assertThat(authenticationUser.isAccountNonLocked()).isTrue();
    }

    @Test
    public void testIsCredentialsNonExpired() {
        assertThat(authenticationUser.isCredentialsNonExpired()).isTrue();
    }

    @Test
    public void testIsEnabled() {
        assertThat(authenticationUser.isEnabled()).isTrue();
    }
}
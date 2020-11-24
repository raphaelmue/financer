package org.financer.shared.domain.model.value.objects;

import org.financer.util.security.Hash;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class HashedPasswordTest {

    @Test
    public void testConstructor() {
        HashedPassword hashedPassword = new HashedPassword("password");
        assertThat(hashedPassword.getHashedPassword()).isEqualTo(Hash.create("password", hashedPassword.getSalt()));

        hashedPassword = new HashedPassword(Hash.create("password", "salt"), "salt");
        assertThat(hashedPassword.getHashedPassword()).isEqualTo(Hash.create("password", "salt"));
    }

    @Test
    public void testIsEqualTo() {
        HashedPassword hashedPassword = new HashedPassword(Hash.create("password", "salt"), "salt");
        assertThat(hashedPassword.isEqualTo("password")).isTrue();
    }
}
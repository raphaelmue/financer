package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class TokenStringTest {

    @Test
    public void testConstructor() {
        assertThat(new TokenString().getToken()).hasSize(64);
    }

}
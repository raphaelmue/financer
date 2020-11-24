package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("unit")
class EmailTest {

    @Test
    public void testConstructor() {
        new Email("test@test.com");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Email("invalidEmail"));
    }

}
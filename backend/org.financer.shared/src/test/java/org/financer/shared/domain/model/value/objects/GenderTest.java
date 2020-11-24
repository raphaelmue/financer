package org.financer.shared.domain.model.value.objects;

import org.financer.shared.exceptions.EnumNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("unit")
public class GenderTest {

    @Test
    public void testConstructor() {
        new Gender("male");
        assertThatExceptionOfType(EnumNotFoundException.class).isThrownBy(() -> new Gender("invalidEnum"));
    }

    @Test
    public void testGetAll() {
        assertThat(Gender.getAll()).hasSize(3);
    }
}
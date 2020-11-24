package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class BirthDateTest extends MockData {

    @Test
    public void testFormat() {
        assertThat(new BirthDate(LocalDate.of(2000, 01, 01)).format(settings())).isEqualTo("01.01.2000");
    }
}
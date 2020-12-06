package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class ValueDateTest extends MockData {

    @Test
    public void testIsInSameMonth() {
        assertThat(new ValueDate(LocalDate.of(2000, 1, 1)).isInSameMonth(new ValueDate(LocalDate.of(2000, 1, 31)))).isTrue();
    }

    @Test
    public void testGetMonthDifference() {
        assertThat(new ValueDate(LocalDate.of(2000, 1, 1)).getMonthDifference(new ValueDate(LocalDate.of(2000, 3, 31)))).isEqualTo(2);
    }

    @Test
    void format() {
        assertThat(new ValueDate(LocalDate.of(2000, 1, 1)).format(settings())).isEqualTo("01.01.2000");
    }
}
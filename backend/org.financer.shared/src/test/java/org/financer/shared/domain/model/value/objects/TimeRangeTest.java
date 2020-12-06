package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class TimeRangeTest extends MockData {

    @Test
    public void testIncludes() {
        assertThat(new TimeRange(LocalDate.now().minusMonths(1)).includes()).isTrue();
        assertThat(new TimeRange(LocalDate.now().plusMonths(1)).includes()).isFalse();

        assertThat(new TimeRange(LocalDate.now().minusMonths(2)).includes(new ValueDate(LocalDate.now().minusMonths(1)))).isTrue();
        assertThat(new TimeRange(LocalDate.now()).includes(new ValueDate(LocalDate.now().minusMonths(1)))).isFalse();
    }

    @Test
    public void testGetMonthIntersection() {
        assertThat(new TimeRange(LocalDate.now().minusMonths(3), LocalDate.now()).getMonthIntersection(new TimeRange(LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1))))
                .isEqualTo(new TimeRange(LocalDate.now().minusMonths(1), LocalDate.now()));
    }

    @Test
    public void testGetMonthDifference() {
        assertThat(new TimeRange().getMonthDifference()).isEqualTo(Integer.MAX_VALUE);
        assertThat(new TimeRange(LocalDate.now().minusMonths(3), LocalDate.now()).getMonthDifference()).isEqualTo(3);
    }

    @Test
    void testFormat() {
        assertThat(new TimeRange(LocalDate.of(2000, 1, 1), LocalDate.of(2001, 1, 1)).format(settings())).isEqualTo("01.01.2000 - 01.01.2001");
    }

    @Test
    void getEndDateOrCurrentDate() {
        assertThat(new TimeRange(LocalDate.now().minusMonths(2)).getEndDateOrCurrentDate()).isEqualTo(LocalDate.now());
        assertThat(new TimeRange(LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(1)).getEndDateOrCurrentDate()).isEqualTo(LocalDate.now().plusMonths(1));
    }

    @Test
    void terminates() {
        assertThat(new TimeRange(LocalDate.now().minusMonths(1)).terminates()).isFalse();
        assertThat(new TimeRange(LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1)).terminates()).isTrue();

    }
}
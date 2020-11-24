package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class ExpireDateTest {

    @Test
    public void isValid() {
        assertThat(new ExpireDate().isValid()).isTrue();
        assertThat(new ExpireDate(LocalDate.now().minusMonths(2)).isValid()).isFalse();
    }

    @Test
    public void update() {
        assertThat(new ExpireDate(LocalDate.now().minusMonths(2)).update().isValid()).isTrue();
    }
}
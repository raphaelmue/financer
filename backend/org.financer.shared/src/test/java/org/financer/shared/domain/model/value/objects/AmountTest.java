package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("unit")
class AmountTest extends MockData {

    @Test
    public void testIsPositive() {
        assertThat(new Amount(10).isPositive()).isTrue();
        assertThat(new Amount(-10).isPositive()).isFalse();
    }

    @Test
    public void testIsNegative() {
        assertThat(new Amount(-10).isNegative()).isTrue();
        assertThat(new Amount(10).isNegative()).isFalse();
    }

    @Test
    public void testIsNotNull() {
        assertThat(new Amount(10).isNotNull()).isTrue();
        assertThat(new Amount(0).isNotNull()).isFalse();
    }

    @Test
    public void testCalculate() {
        assertThat(new Amount(10).calculate(new Quantity(10))).isEqualTo(new Amount(100));
    }

    @Test
    public void testAdjustSign() {
        assertThat(new Amount(10).adjustSign()).isEqualTo(new Amount(-10));
    }

    @Test
    public void testAdd() {
        assertThat(new Amount(10).add(new Amount(10))).isEqualTo(new Amount(20));
    }

    @Test
    public void testSubtract() {
        assertThat(new Amount(20).subtract(new Amount(10))).isEqualTo(new Amount(10));
        assertThat(new Amount(20).subtract(10)).isEqualTo(new Amount(10));
    }

    @Test
    public void testMultiply() {
        assertThat(new Amount(10).multiply(new Amount(10))).isEqualTo(new Amount(100));
        assertThat(new Amount(10).multiply(10)).isEqualTo(new Amount(100));
    }

    @Test
    public void testDivide() {
        assertThat(new Amount(100).divide(new Amount(10))).isEqualTo(new Amount(10));
        assertThat(new Amount(100).divide(10)).isEqualTo(new Amount(10));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Amount(10).divide(new Amount()));
    }

    @Test
    public void testCalcRatio() {
        assertThat(new Amount(10).calcRatio(new Amount(100))).isEqualTo(new Amount(10));
    }

    @Test
    public void testFormat() {
        assertThat(new Amount(10.2).format(settings())).isEqualTo("10,20 EUR");
    }
}
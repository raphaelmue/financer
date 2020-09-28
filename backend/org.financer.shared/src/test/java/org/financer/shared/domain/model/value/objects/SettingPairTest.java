package org.financer.shared.domain.model.value.objects;

import org.financer.shared.exceptions.EnumNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("unit")
public class SettingPairTest {

    @Test
    public void testConstructor() {
        assertThat(new SettingPair("currency", "EUR")).isNotNull();
        assertThat(new SettingPair(SettingPair.Property.CURRENCY, "EUR")).isNotNull();
    }

    @Test
    public void testConstructorThrowsEnumNotFoundException() {
        assertThatExceptionOfType(EnumNotFoundException.class).isThrownBy(() ->
                new SettingPair("invalidProperty", "value"));
    }

}
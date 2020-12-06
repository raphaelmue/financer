package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("unit")
public class IPAddressTest {

    @Test
    public void testConstructor() {
        new IPAddress("192.168.0.1");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new IPAddress("invalidIPAddress"));
    }

}
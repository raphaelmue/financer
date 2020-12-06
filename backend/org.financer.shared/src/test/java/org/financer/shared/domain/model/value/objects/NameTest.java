package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class NameTest extends MockData {

    @Test
    public void testFormat() {
        assertThat(new Name("Test", "User").format(settings())).isEqualTo("Test User");
    }
}
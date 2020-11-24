package org.financer.server.domain.model.transaction;

import org.financer.server.utils.SpringTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class AttachmentTest extends SpringTest {

    private Attachment attachment;

    @BeforeEach
    public void setUp() {
        attachment = attachment();
    }

    @Test
    public void testIsPropertyOfUser() {
        assertThat(attachment.isPropertyOfUser(1)).isTrue();
        assertThat(attachment.isPropertyOfUser(2)).isFalse();
    }
}

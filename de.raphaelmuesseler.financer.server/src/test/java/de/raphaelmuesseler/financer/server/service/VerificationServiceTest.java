package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.shared.model.user.User;
import org.apache.commons.mail.EmailException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("skip")
public class VerificationServiceTest {
    private final VerificationService verificationService = new VerificationService("smtp.ionos.de",
            587,
            "info@financer-project.org",
            null);

    @Test
    public void testSendVerificationEmail() throws EmailException {
        User user = new User();
        user.setEmail("info@financer-project.org");

        verificationService.sendVerificationEmail(user);
    }
}

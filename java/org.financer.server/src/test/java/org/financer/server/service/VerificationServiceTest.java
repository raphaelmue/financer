package org.financer.server.service;

import org.apache.commons.mail.EmailException;
import org.financer.server.configuration.PersistenceConfiguration;
import org.financer.server.configuration.ServiceConfiguration;
import org.financer.shared.model.user.User;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("WeakerAccess")
@Tag("skip")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceConfiguration.class, PersistenceConfiguration.class})
public class VerificationServiceTest {

    @Autowired
    private VerificationService verificationService;

    @Test
    public void testSendVerificationEmail() throws EmailException {
        User user = new User();
        user.setEmail("info@financer-project.org");

        verificationService.sendVerificationEmail(user);
    }
}

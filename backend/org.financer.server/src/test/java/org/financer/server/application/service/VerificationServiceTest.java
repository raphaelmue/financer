package org.financer.server.application.service;

import org.financer.server.application.configuration.PersistenceConfiguration;
import org.financer.server.application.configuration.ServiceConfiguration;
import org.junit.jupiter.api.Tag;
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

//    @Test
//    public void testSendVerificationEmail() throws EmailException {
//        User user = new User();
//        user.setEmail("info@financer-project.org");
//
//        verificationService.sendVerificationEmail(user);
//    }
}

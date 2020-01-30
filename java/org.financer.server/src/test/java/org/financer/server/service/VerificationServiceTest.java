package org.financer.server.service;

import org.apache.commons.mail.EmailException;
import org.financer.server.db.HibernateUtil;
import org.financer.shared.model.user.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("WeakerAccess")
@Tag("skip")
public class VerificationServiceTest {

    private static VerificationService verificationService;

    @BeforeAll
    public static void setup() throws IOException {
        Properties testProperties = new Properties();
        testProperties.load(FinancerServiceTest.class.getResourceAsStream("test.properties"));
        HibernateUtil.setDatabaseProperties(testProperties);
        verificationService = new VerificationService(testProperties);
    }

    @Test
    public void testSendVerificationEmail() throws EmailException {
        User user = new User();
        user.setEmail("info@financer-project.org");

        verificationService.sendVerificationEmail(user);
    }
}

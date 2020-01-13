package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.shared.model.user.User;
import org.apache.commons.mail.EmailException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

@Tag("skip")
public class VerificationServiceTest {

    private VerificationService verificationService;

    @BeforeAll
    public void setup() throws IOException {
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

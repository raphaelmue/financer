package org.financer.server.application.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.financer.server.domain.model.user.UserEntity;
import org.financer.server.domain.model.user.VerificationTokenEntity;
import org.financer.util.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(FinancerService.class);

    private String host;
    private int port;
    private String email;
    private String password;

    private final RandomString tokenGenerator = new RandomString(128);

    @Autowired
    public VerificationService() {
        if (System.getProperty("financer.server.smpt").equals("true")) {
            this.host = System.getProperty("financer.server.smtp.host");
            this.port = Integer.parseInt(System.getProperty("financer.server.smtp.host"));
            this.email = System.getProperty("financer.server.smtp.email");
            this.password = System.getProperty("financer.server.smtp.password");
        }
    }

    public void sendVerificationEmail(UserEntity user, VerificationTokenEntity tokenEntity) throws EmailException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FinancerService.class.getResourceAsStream("verification-email.html")))) {
            for (String line; (line = reader.readLine()) != null; ) {
                content.append(line);
            }
        } catch (IOException e) {
            logger.error("Verification email is not available.", e);
        }

        if (this.host != null) {
            Email verificationEmail = new HtmlEmail();
            verificationEmail.setHostName(this.host);
            verificationEmail.setSmtpPort(this.port);
            verificationEmail.setAuthenticator(new DefaultAuthenticator(this.email, this.password));
            verificationEmail.setFrom(this.email, "Financer Project");
            verificationEmail.setSubject("Verify your account!");
            verificationEmail.setMsg(String.format(content.toString().replaceAll("\\s{2,}", " "), tokenEntity.getToken().getToken()));
            verificationEmail.addTo(user.getEmail().getEmailAddress());

            verificationEmail.send();
        }
    }
}

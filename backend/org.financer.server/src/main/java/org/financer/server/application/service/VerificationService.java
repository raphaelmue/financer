package org.financer.server.application.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.model.user.VerificationToken;
import org.financer.util.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    private String host;
    private int port;
    private String email;
    private String password;

    private final RandomString tokenGenerator = new RandomString(128);

    public VerificationService() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/application.properties"));
        if (properties.get("financer.server.smtp").equals("true")) {
            this.host = properties.getProperty("financer.server.smtp.host");
            this.port = Integer.parseInt(properties.getProperty("financer.server.smtp.host"));
            this.email = properties.getProperty("financer.server.smtp.email");
            this.password = properties.getProperty("financer.server.smtp.password");
        }
    }

    public void sendVerificationEmail(User user, VerificationToken tokenEntity) throws EmailException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(VerificationService.class.getResourceAsStream("verification-email.html")))) {
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

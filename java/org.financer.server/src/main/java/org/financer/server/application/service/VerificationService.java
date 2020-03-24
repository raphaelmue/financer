package org.financer.server.application.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.financer.shared.model.user.VerificationToken;
import org.financer.util.RandomString;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class VerificationService {

    private final String host;
    private final int port;
    private final String email;
    private final String password;

    private final RandomString tokenGenerator = new RandomString(128);

    public VerificationService(String host, int port, String email, String password) {
        this.host = host;
        this.port = port;
        this.email = email;
        this.password = password;
    }

    VerificationToken sendVerificationEmail(User user) throws EmailException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FinancerService.class.getResourceAsStream("verification-email.html")))) {
            for (String line; (line = reader.readLine()) != null; ) {
                content.append(line);
            }
        } catch (IOException e) {
            Logger.getLogger("Financer Server").log(Level.SEVERE, e.getMessage(), e);
        }

        String verificationToken = tokenGenerator.nextString();

        if (this.host != null) {
            Email verificationEmail = new HtmlEmail();
            verificationEmail.setHostName(this.host);
            verificationEmail.setSmtpPort(this.port);
            verificationEmail.setAuthenticator(new DefaultAuthenticator(this.email, this.password));
            verificationEmail.setFrom(this.email, "Financer Project");
            verificationEmail.setSubject("Verify your account!");
            verificationEmail.setMsg(String.format(content.toString().replaceAll("\\s{2,}", " "), verificationToken));
            verificationEmail.addTo(user.getEmail());

            verificationEmail.send();
        }

        return new VerificationToken(0, user, verificationToken, LocalDate.now().plusMonths(1));
    }
}

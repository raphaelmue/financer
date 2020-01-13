package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.RandomString;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VerificationService {

    private final String host;
    private final int port;
    private final String email;
    private final String password;

    private final RandomString tokenGenerator = new RandomString(128);

    public VerificationService(Properties properties) {
        this.host = properties.getProperty("financer.server.smtp.host");
        this.port = Integer.parseInt(properties.getProperty("financer.server.smtp.port"));
        this.email = properties.getProperty("financer.server.smtp.email");
        this.password = properties.getProperty("financer.server.smtp.password");
    }

    String sendVerificationEmail(User user) throws EmailException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(FinancerService.class.getResourceAsStream("verification-email.html")))) {
            for (String line; (line = reader.readLine()) != null;) {
                content.append(line);
            }
        } catch (IOException e) {
            Logger.getLogger("Financer Server").log(Level.SEVERE, e.getMessage(), e);
        }

        String verificationToken = tokenGenerator.nextString();

        Email verificationEmail = new HtmlEmail();
        verificationEmail.setHostName(this.host);
        verificationEmail.setSmtpPort(this.port);
        verificationEmail.setAuthenticator(new DefaultAuthenticator(this.email, this.password));
        verificationEmail.setFrom(this.email, "Financer Project");
        verificationEmail.setSubject("Verify your account!");
        verificationEmail.setMsg(String.format(content.toString().replaceAll("\\s{2,}"," "), verificationToken));
        verificationEmail.addTo(user.getEmail());

        verificationEmail.send();

        return verificationToken;
    }
}

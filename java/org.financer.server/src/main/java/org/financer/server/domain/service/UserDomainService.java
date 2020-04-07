package org.financer.server.domain.service;

import org.apache.commons.mail.EmailException;
import org.financer.server.application.api.error.UniqueEmailViolationException;
import org.financer.server.application.service.VerificationService;
import org.financer.server.domain.model.user.Token;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.model.user.VerificationToken;
import org.financer.server.domain.repository.TokenRepository;
import org.financer.server.domain.repository.UserRepository;
import org.financer.server.domain.repository.VerificationTokenRepository;
import org.financer.shared.domain.model.value.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserDomainService {

    private static final Logger logger = LoggerFactory.getLogger(UserDomainService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private VerificationService verificationService;

    /**
     * Checks, if the users credentials are correct
     *
     * @param email     email of the user
     * @param password  password to be checked
     * @param ipAddress IP address of the users client
     * @param system    operating system of the users client
     * @return User object, if credentials are correct, null otherwise
     */
    public Optional<User> checkCredentials(String email, String password, IPAddress ipAddress, OperatingSystem system) {
        logger.info("Checking users credentials.");
        Optional<User> userOptional = userRepository.findByEmail(new Email(email));
        if (userOptional.isPresent() && userOptional.get().getPassword().isEqualTo(password)) {
            logger.info("Credentials of user [{}, '{}'] are approved.",
                    userOptional.get().getId(), userOptional.get().getName());
            this.generateOrUpdateToken(userOptional.get(), ipAddress, system);
            return userOptional;
        }
        logger.info("Failed to authenticate: Credentials are incorrect.");
        return Optional.empty();
    }

    /**
     * Checks, whether the token is valid and not expired and returns the corresponding user.
     *
     * @param tokenString token to be checked
     * @return User that owns this token
     */
    public Optional<User> checkUsersToken(TokenString tokenString) {
        logger.info("Checking users token.");

        Optional<Token> tokenOptional = tokenRepository.getTokenByToken(tokenString);
        if (tokenOptional.isPresent() && tokenOptional.get().getExpireDate().isValid()) {
            logger.info("Token of user [{}, '{}'] is approved",
                    tokenOptional.get().getUser().getId(), tokenOptional.get().getUser().getName());
            return Optional.of(tokenOptional.get().getUser()
                    .setActiveToken(tokenOptional.get()));
        }
        return Optional.empty();
    }

    /**
     * Registers a new user and stores it into database. If the email is already assigned to another user, an {@link
     * UniqueEmailViolationException} exception is thrown. Furthermore a new token and verification token is generated.
     *
     * @param user            user to be inserted
     * @param ipAddress       IP address of the client
     * @param operatingSystem operating system of the client
     */
    public User registerUser(User user, IPAddress ipAddress, OperatingSystem operatingSystem) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UniqueEmailViolationException(user.getEmail());
        }

        User result = userRepository.save(user);
        this.generateOrUpdateToken(result, ipAddress, operatingSystem);
        this.generateVerificationToken(result);
        return result;
    }

    /**
     * Generates a new token or updates the token, if the IP address is already store in the database, and stores it in
     * the database. The token is added to the given user object.
     *
     * @param user            user
     * @param ipAddress       ip address of client
     * @param operatingSystem operating system of the client
     */
    private void generateOrUpdateToken(User user, IPAddress ipAddress, OperatingSystem operatingSystem) {
        Token token;
        Optional<Token> tokenOptional = tokenRepository.getTokenByIPAddress(user.getId(), ipAddress);
        if (tokenOptional.isPresent()) {
            tokenOptional.get().isPropertyOfUser(user);

            // check if token is not expired
            if (tokenOptional.get().getExpireDate().isValid()) {
                // update expire date
                tokenOptional.get().setExpireDate(tokenOptional.get().getExpireDate().update());
                token = tokenRepository.save(tokenOptional.get());
            } else {
                throw new IllegalStateException("The given token is not valid.");
            }
        } else {
            token = tokenRepository.save(
                    new Token()
                            .setUser(user)
                            .setToken(new TokenString())
                            .setIpAddress(ipAddress)
                            .setExpireDate(new ExpireDate())
                            .setOperatingSystem(operatingSystem));
        }
        user.setActiveToken(token);
        user.getTokens().add(token);
    }

    /**
     * Generates a verification token and sends it to the respective email address.
     *
     * @param user user for which the verification token should be generated
     */
    private void generateVerificationToken(User user) {
        VerificationToken verificationToken = verificationTokenRepository.save(
                new VerificationToken()
                        .setUser(user)
                        .setToken(new TokenString())
                        .setExpireDate(new ExpireDate()));
        user.setVerificationToken(verificationToken);
        try {
            verificationService.sendVerificationEmail(user, verificationToken);
        } catch (EmailException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Verifying a users token.
     *
     * @param verificationToken verification token string
     * @return updated user or null if token is invalid
     */
    public Optional<User> verifyUser(TokenString verificationToken) {
        logger.info("Verifying new user.");

        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(verificationToken);
        if (tokenOptional.isPresent() && tokenOptional.get().getExpireDate().isValid()) {
            User user = tokenOptional.get().getUser();
            tokenOptional.get().setVerifyingDate(LocalDate.now());
            verificationTokenRepository.save(tokenOptional.get());
            user.setVerificationToken(tokenOptional.get());
            logger.info("User [{}, '{}'] is verified.", user.getId(), user.getName());
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Deletes a users token by the given id. Checks whether the given user owns the given token.
     *
     * @param userId  id of the user who owns the token
     * @param tokenId id of the token to delete
     * @return true if operation was successful
     */
    public boolean deleteToken(long userId, long tokenId) {
        logger.info("Deleting token. ");
        Optional<Token> tokenOptional = tokenRepository.findById(tokenId);
        if (tokenOptional.isPresent()) {
            tokenOptional.get().throwIfNotUsersProperty(userId);
            tokenRepository.delete(tokenOptional.get());
            return true;
        }
        return false;
    }

    /**
     * Updates the users password. First of all, the old password is verfied. Then the new password will be hashed after
     * concatenating it with a new generated salt.
     *
     * @param userId          id of user whose password will be update
     * @param oldPassword     old password of the user in order to verify this operation
     * @param updatedPassword new plain password
     * @return true if operation was successful
     */
    public boolean updatePassword(long userId, String oldPassword, String updatedPassword) {
        logger.info("Updating users password. ");
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent() && userOptional.get().getPassword().isEqualTo(oldPassword)) {
            userOptional.get().setPassword(new HashedPassword(updatedPassword));
            userRepository.save(userOptional.get());
            return true;
        }
        return false;
    }
}

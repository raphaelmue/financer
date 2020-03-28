package org.financer.server.domain.service;

import org.apache.commons.mail.EmailException;
import org.financer.server.application.service.VerificationService;
import org.financer.server.domain.model.user.TokenEntity;
import org.financer.server.domain.model.user.UserEntity;
import org.financer.server.domain.model.user.VerificationTokenEntity;
import org.financer.server.domain.repository.TokenRepository;
import org.financer.server.domain.repository.UserRepository;
import org.financer.server.domain.repository.VerificationTokenRepository;
import org.financer.shared.domain.model.value.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Optional<UserEntity> checkCredentials(String email, String password, IPAddress ipAddress, OperatingSystem system) {
        logger.info("Checking users credentials.");
        Optional<UserEntity> userOptional = userRepository.findByEmail(new Email(email));
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
    public Optional<UserEntity> checkUsersToken(TokenString tokenString) {
        logger.info("Checking users token.");

        Optional<TokenEntity> tokenOptional = tokenRepository.getTokenByToken(tokenString);
        if (tokenOptional.isPresent() && tokenOptional.get().getExpireDate().isValid()) {
            logger.info("Token of user [{}, '{}'] is approved",
                    tokenOptional.get().getUser().getId(), tokenOptional.get().getUser().getName());
            return Optional.of(tokenOptional.get().getUser());
        }
        logger.info("Failed to authenticate: Token is invalid.");
        return Optional.empty();
    }

    /**
     * Registers a new user and stores it into database.
     *
     * @param user            user to be inserted
     * @param ipAddress       IP address of the client
     * @param operatingSystem operating system of the client
     */
    public UserEntity registerUser(UserEntity user, IPAddress ipAddress, OperatingSystem operatingSystem) {
        UserEntity result = userRepository.save(user);
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
    private void generateOrUpdateToken(UserEntity user, IPAddress ipAddress, OperatingSystem operatingSystem) {
        TokenEntity tokenEntity;
        Optional<TokenEntity> tokenOptional = tokenRepository.getTokenByIPAddress(ipAddress);
        if (tokenOptional.isPresent()) {
            if (tokenOptional.get().getExpireDate().isValid()) {
                // update expire date
                tokenOptional.get().setExpireDate(tokenOptional.get().getExpireDate().update());
                tokenEntity = tokenRepository.save(tokenOptional.get());
            } else {
                throw new IllegalStateException("The given token is not valid.");
            }
        } else {
            tokenEntity = tokenRepository.save(
                    new TokenEntity()
                            .setUser(user)
                            .setToken(new TokenString())
                            .setIpAddress(ipAddress)
                            .setExpireDate(new ExpireDate())
                            .setOperatingSystem(operatingSystem));
        }
        user.getTokens().add(tokenEntity);
    }

    /**
     * Generates a verification token and sends it to the respective email address.
     *
     * @param user user for which the verification token should be generated
     */
    private void generateVerificationToken(UserEntity user) {
        VerificationTokenEntity verificationToken = verificationTokenRepository.save(
                new VerificationTokenEntity()
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
    @Transactional
    public Optional<UserEntity> verifyUser(TokenString verificationToken) {
        logger.info("Verifying new user.");

        Optional<VerificationTokenEntity> tokenOptional = verificationTokenRepository.findByToken(verificationToken);
        if (tokenOptional.isPresent() && tokenOptional.get().getExpireDate().isValid()) {
            UserEntity userEntity = tokenOptional.get().getUser();
            tokenOptional.get().setVerifyingDate(LocalDate.now());
            verificationTokenRepository.save(tokenOptional.get());
            userEntity.setVerificationToken(tokenOptional.get());
            logger.info("User [{}, '{}'] is verified.", userEntity.getId(), userEntity.getName());
            return Optional.of(userEntity);
        }
        return Optional.empty();
    }
}

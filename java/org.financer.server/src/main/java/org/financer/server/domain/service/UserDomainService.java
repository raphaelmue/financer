package org.financer.server.domain.service;

import org.financer.server.domain.model.user.TokenEntity;
import org.financer.server.domain.model.user.UserEntity;
import org.financer.server.domain.repository.TokenEntityRepository;
import org.financer.server.domain.repository.UserEntityRepository;
import org.financer.shared.domain.model.value.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDomainService {

    private static final Logger logger = LoggerFactory.getLogger(UserDomainService.class);

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private TokenEntityRepository tokenEntityRepository;

    /**
     * Checks, if the users credentials are correct
     *
     * @param email     email of the user
     * @param password  password to be checked
     * @param ipAddress IP address of the users client
     * @param system    operating system of the users client
     * @param isMobile  defines whether operating system is a mobile device
     * @return User object, if credentials are correct, null otherwise
     */
    public Optional<UserEntity> checkCredentials(String email, String password, String ipAddress, String system, boolean isMobile) {
        logger.info("Checking users credentials.");
        Optional<UserEntity> userOptional = userEntityRepository.findByEmail(new Email(email));
        if (userOptional.isPresent() && userOptional.get().getPassword().isEqualTo(password)) {
            logger.info("Credentials of user [{}, '{}'] are approved.",
                    userOptional.get().getId(), userOptional.get().getName());
            return userOptional;
        }
        logger.info("Credentials are incorrect.");
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

        Optional<TokenEntity> tokenOptional = tokenEntityRepository.getTokenByToken(tokenString);
        if (tokenOptional.isPresent() && tokenOptional.get().getExpireDate().isValid()) {
            logger.info("Token of user [{}, '{}'] is approved",
                    tokenOptional.get().getUser().getId(), tokenOptional.get().getUser().getName());
            return Optional.of(tokenOptional.get().getUser());
        }
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
        UserEntity result = userEntityRepository.save(user);
        this.generateOrUpdateToken(result, ipAddress, operatingSystem);
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
        Optional<TokenEntity> tokenOptional = tokenEntityRepository.getTokenByIPAddress(ipAddress);
        if (tokenOptional.isPresent()) {
            if (tokenOptional.get().getExpireDate().isValid()) {
                // update expire date
                tokenOptional.get().setExpireDate(tokenOptional.get().getExpireDate().update());
                tokenEntity = tokenEntityRepository.save(tokenOptional.get());
            } else {
                throw new IllegalStateException("The given token is not valid");
            }
        } else {
            tokenEntity = tokenEntityRepository.save(
                    new TokenEntity()
                            .setUser(user)
                            .setToken(new TokenString())
                            .setIpAddress(ipAddress)
                            .setExpireDate(new ExpireDate())
                            .setOperatingSystem(operatingSystem));
        }
        user.getTokens().add(tokenEntity);
    }
}

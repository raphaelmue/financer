package org.financer.server.domain.service;

import org.apache.commons.mail.EmailException;
import org.financer.server.application.api.error.UnauthorizedOperationException;
import org.financer.server.application.api.error.UnauthorizedTokenException;
import org.financer.server.application.api.error.UniqueEmailViolationException;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.application.service.VerificationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.FixedTransaction;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.server.domain.model.user.Token;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.model.user.VerificationToken;
import org.financer.server.domain.repository.*;
import org.financer.shared.domain.model.value.objects.*;
import org.financer.util.collections.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.financer.server.domain.repository.VariableTransactionRepository.PAGE_SIZE;

@Service
public class UserDomainService {

    private static final Logger logger = LoggerFactory.getLogger(UserDomainService.class);

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final CategoryRepository categoryRepository;
    private final VariableTransactionRepository variableTransactionRepository;
    private final FixedTransactionRepository fixedTransactionRepository;
    private final VerificationService verificationService;

    public UserDomainService(AuthenticationService authenticationService, UserRepository userRepository,
                             TokenRepository tokenRepository, VerificationTokenRepository verificationTokenRepository,
                             CategoryRepository categoryRepository, VariableTransactionRepository variableTransactionRepository,
                             FixedTransactionRepository fixedTransactionRepository, VerificationService verificationService) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.categoryRepository = categoryRepository;
        this.variableTransactionRepository = variableTransactionRepository;
        this.fixedTransactionRepository = fixedTransactionRepository;
        this.verificationService = verificationService;
    }

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
                throw new UnauthorizedTokenException(tokenOptional.get().getToken());
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
     * @param tokenId id of the token to delete
     */
    public void deleteToken(long tokenId) {
        logger.info("Deleting token. ");
        Optional<Token> tokenOptional = tokenRepository.findById(tokenId);
        if (tokenOptional.isPresent()) {
            tokenOptional.get().throwIfNotUsersProperty(authenticationService.getUserId());
            tokenRepository.delete(tokenOptional.get());
        }
    }

    /**
     * Updates the users password. First of all, the old password is verified. Then the new password will be hashed
     * after concatenating it with a new generated salt.
     *
     * @param updatedPassword new hashed password
     * @return update user object
     */
    public User updatePassword(HashedPassword updatedPassword) {
        logger.info("Updating users password.");
        Optional<User> userOptional = userRepository.findById(authenticationService.getUserId());
        if (userOptional.isPresent()) {
            userOptional.get().setPassword(updatedPassword);
            return userRepository.save(userOptional.get());
        }
        throw new UnauthorizedOperationException(authenticationService.getUserId());
    }

    /**
     * Updates the users personal information with given values.
     *
     * <p> The values are validated, before updating the user. If the given parameters are null or equal to the
     * user that will be updated, they will be ignored in the updating process. If no changes are applied to the user,
     * the user is returned.</p>
     *
     * @param name      updated name
     * @param birthDate updated birth date
     * @param gender    updated gender
     * @return updated user
     */
    public User updatePersonalInformation(Name name, BirthDate birthDate, Gender gender) {
        logger.info("Updating personal information of user {}", authenticationService.getUserId());
        User user = authenticationService.getAuthenticatedUser();

        boolean userChanged = changeUserName(user, name)
                | changeUserBirthDate(user, birthDate)
                | changeUserGender(user, gender);

        if (userChanged) {
            return userRepository.save(user);
        }
        return user;
    }

    private boolean changeUserName(User user, Name name) {
        if (name != null && user.getName() != name) {
            user.setName(name);
            return true;
        }
        return false;
    }

    private boolean changeUserBirthDate(User user, BirthDate birthDate) {
        if (birthDate != null && user.getBirthDate() != birthDate) {
            user.setBirthDate(birthDate);
            return true;
        }
        return false;
    }

    private boolean changeUserGender(User user, Gender gender) {
        if (gender != null && (user.getGender() == null || gender != user.getGender())) {
            user.setGender(gender);
            return true;
        }
        return false;
    }


    /**
     * Updates the users settings, i.e. either the property's value is update or the property is added to settings.
     *
     * @param setting updated settings
     * @return update user
     */
    public User updateUsersSettings(Map<SettingPair.Property, String> setting) {
        User user = authenticationService.getAuthenticatedUser();
        setting.forEach(user::putOrUpdateSettingProperty);
        return userRepository.save(user);
    }

    /**
     * Fetches all categories of the user and returns them as a tree.
     *
     * @return list root categories.
     */
    public List<Category> fetchCategories() {
        return Iterables.toList(categoryRepository.findAllByUserId(authenticationService.getUserId()));
    }

    /**
     * Returns a list of variable transactions that belong to a user.
     *
     * @param page page number that is fetched
     * @return list of variable transactions.
     */
    public List<VariableTransaction> fetchVariableTransactions(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("valueDate.date").descending());
        return Iterables.toList(variableTransactionRepository.findByCategoryUserId(authenticationService.getUserId(), pageable));
    }

    /**
     * Returns a list of all fixed transactions that belong to a user.
     *
     * @return list of fixed transactions
     */
    public List<FixedTransaction> fetchFixedTransactions() {
        return Iterables.toList(fixedTransactionRepository.findAllActiveTransactionsByUserId(authenticationService.getUserId()));
    }
}

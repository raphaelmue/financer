package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.transaction.FixedTransaction;
import org.financer.client.domain.model.transaction.VariableTransaction;
import org.financer.client.domain.model.user.User;
import org.financer.shared.domain.model.api.user.RegisterUserDTO;
import org.financer.shared.domain.model.api.user.UpdatePersonalInformationDTO;
import org.financer.shared.domain.model.api.user.UpdateSettingsDTO;

import java.util.List;

public interface UserRestApi {

    /**
     * Logs the user into the system.
     *
     * @param email    email of the user
     * @param password password of the user
     */
    void loginUser(String email, String password, RestCallback<User> callback);

    /**
     * Deletes a token. This is called, when the user logs out of a client.
     *
     * @param userId  id of the user
     * @param tokenId id of the token to delete
     */
    void deleteToken(Long userId, Long tokenId, RestCallback<User> callback);

    /**
     * Registers a new user.
     */
    void registerUser(RegisterUserDTO registerUserDTO, RestCallback<User> callback);

    /**
     * Updates the password of the given user.
     *
     * @param userId      id of the user
     * @param oldPassword old password of the user to verify
     * @param newPassword updated unencrypted password
     */
    void updateUsersPassword(Long userId, String oldPassword, String newPassword, RestCallback<User> callback);

    /**
     * Updates the users personal information
     *
     * @param userId              id of the user
     * @param personalInformation updated personal information
     */
    void updateUsersPersonalInformation(Long userId, UpdatePersonalInformationDTO personalInformation, RestCallback<User> callback);

    /**
     * Updates users settings.
     *
     * @param userId  id of the user
     * @param setting updated settings
     */
    void updateUsersSettings(Long userId, UpdateSettingsDTO setting, RestCallback<User> callback);

    /**
     * Verifies a users email address by checking the given verification token.
     *
     * @param userId            id of the user to check
     * @param verificationToken verification token to check
     */
    void verifyUser(Long userId, String verificationToken, RestCallback<User> callback);

    /**
     * Fetches the users categories.
     *
     * @param userId user id
     */
    void getUsersCategories(Long userId, RestCallback<List<Category>> callback);


    /**
     * Fetches the users variable transactions.
     *
     * @param userId user id
     */
    void getUsersVariableTransactions(Long userId, int page, RestCallback<List<VariableTransaction>> callback);

    /**
     * Fetches the users fixed transactions.
     *
     * @param userId user id
     */
    void getUsersFixedTransactions(Long userId, RestCallback<List<FixedTransaction>> callback);
}

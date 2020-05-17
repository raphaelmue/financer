package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.transaction.FixedTransaction;
import org.financer.client.domain.model.transaction.VariableTransaction;
import org.financer.client.domain.model.user.Setting;
import org.financer.client.domain.model.user.User;
import org.financer.shared.domain.model.value.objects.HashedPassword;
import org.financer.shared.domain.model.value.objects.SettingPair;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserRestApi {

    /**
     * Logs the user into the system.
     *
     * @param email    email of the user
     * @param password password of the user
     */
    ServerRequestHandler loginUser(String email, String password, RestCallback<User> callback);

    /**
     * Deletes a token. This is called, when the user logs out of a client.
     *
     * @param userId  id of the user
     * @param tokenId id of the token to delete
     */
    ServerRequestHandler deleteToken(Long userId, Long tokenId, RestCallback<Void> callback);

    /**
     * Registers a new user.
     */
    ServerRequestHandler registerUser(User user, RestCallback<User> callback);

    /**
     * Updates the password of the given user.
     *  @param userId      id of the user
     * @param newPassword updated unencrypted password
     */
    ServerRequestHandler updateUsersPassword(Long userId, HashedPassword newPassword, RestCallback<User> callback);

    /**
     * Updates the users personal information
     *
     * @param user
     */
    ServerRequestHandler updateUsersPersonalInformation(User user, RestCallback<User> callback);

    /**
     * Updates users settings.
     *
     * @param userId  id of the user
     * @param setting updated settings
     */
    ServerRequestHandler updateUsersSettings(Long userId, Map<SettingPair.Property, Setting> settings, RestCallback<User> callback);

    /**
     * Verifies a users email address by checking the given verification token.
     *
     * @param userId            id of the user to check
     * @param verificationToken verification token to check
     */

    /**
     * Fetches the users categories.
     *
     * @param userId   user id
     * @param callback
     */
    ServerRequestHandler getUsersCategories(Long userId, RestCallback<Set<Category>> callback);


    /**
     * Fetches the users variable transactions.
     *
     * @param userId user id
     */
    ServerRequestHandler getUsersVariableTransactions(Long userId, int page, RestCallback<List<VariableTransaction>> callback);

    /**
     * Fetches the users fixed transactions.
     *
     * @param userId user id
     */
    ServerRequestHandler getUsersFixedTransactions(Long userId, RestCallback<List<FixedTransaction>> callback);
}

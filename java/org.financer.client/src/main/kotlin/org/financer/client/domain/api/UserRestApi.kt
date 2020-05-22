package org.financer.client.domain.api

import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.transaction.FixedTransaction
import org.financer.client.domain.model.transaction.VariableTransaction
import org.financer.client.domain.model.user.Setting
import org.financer.client.domain.model.user.User
import org.financer.shared.domain.model.value.objects.HashedPassword
import org.financer.shared.domain.model.value.objects.SettingPair

interface UserRestApi {
    /**
     * Logs the user into the system.
     *
     * @param email    email of the user
     * @param password password of the user
     */
    suspend fun loginUser(email: String, password: String): User?

    /**
     * Deletes a token. This is called, when the user logs out of a client.
     *
     * @param userId  id of the user
     * @param tokenId id of the token to delete
     */
    suspend fun deleteToken(userId: Long, tokenId: Long)

    /**
     * Registers a new user.
     */
    suspend fun registerUser(user: User): User?

    /**
     * Updates the password of the given user.
     * @param userId      id of the user
     * @param newPassword updated unencrypted password
     */
    suspend fun updateUsersPassword(userId: Long, newPassword: HashedPassword): User?

    /**
     * Updates the users personal information
     *
     * @param user
     */
    suspend fun updateUsersPersonalInformation(user: User): User?

    /**
     * Updates users settings.
     *
     * @param userId  id of the user
     * @param setting updated settings
     */
    suspend fun updateUsersSettings(userId: Long, settings: Map<SettingPair.Property, Setting>): User?

    /**
     * Fetches the users categories.
     *
     * @param userId   user id
     * @param callback
     */
    suspend fun getUsersCategories(userId: Long): List<Category>

    /**
     * Fetches the users variable transactions.
     *
     * @param userId user id
     */
    suspend fun getUsersVariableTransactions(userId: Long, page: Int = 0): List<VariableTransaction>

    /**
     * Fetches the users fixed transactions.
     *
     * @param userId user id
     */
    suspend fun getUsersFixedTransactions(userId: Long): List<FixedTransaction>
}
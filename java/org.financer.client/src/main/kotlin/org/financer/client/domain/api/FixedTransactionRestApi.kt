package org.financer.client.domain.api

import org.financer.client.domain.model.transaction.FixedTransaction
import org.financer.client.domain.model.transaction.FixedTransactionAmount

interface FixedTransactionRestApi {
    /**
     * Creates a fixed transaction.
     *
     * @param fixedTransaction transaction to be inserted
     */
    suspend fun createFixedTransaction(fixedTransaction: FixedTransaction): FixedTransaction?

    /**
     * Updates a specified transaction.
     *
     * @param fixedTransaction transaction object with updated information
     */
    suspend fun updateFixedTransaction(fixedTransaction: FixedTransaction): FixedTransaction?

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     */
    suspend fun deleteFixedTransaction(transactionId: Long)

    /**
     * Creates a transaction amount to a given transaction.
     *
     * @param transactionId     id of transaction to add transaction amount to
     * @param transactionAmount transaction amount to be inserted
     */
    suspend fun createTransactionAmount(transactionId: Long, transactionAmount: FixedTransactionAmount): FixedTransactionAmount?

    /**
     * Updates a transaction amount.
     * @param transactionAmountId transaction amount id to be updated
     * @param transactionId       transaction id
     * @param transactionAmount   updated transaction amount id
     */
    suspend fun updateTransactionAmount(transactionId: Long, transactionAmount: FixedTransactionAmount): FixedTransactionAmount?

    /**
     * Deletes a transaction amount.
     *
     * @param transactionId       transaction id
     * @param transactionAmountId transaction amount id to be deleted
     */
    suspend fun deleteTransactionAmount(transactionId: Long, transactionAmountId: Long)
}
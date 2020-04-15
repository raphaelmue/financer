package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.domain.model.transaction.FixedTransaction;
import org.financer.client.domain.model.transaction.FixedTransactionAmount;
import org.financer.shared.domain.model.api.transaction.fixed.CreateFixedTransactionAmountDTO;
import org.financer.shared.domain.model.api.transaction.fixed.CreateFixedTransactionDTO;
import org.financer.shared.domain.model.api.transaction.fixed.UpdateFixedTransactionAmountDTO;
import org.financer.shared.domain.model.api.transaction.fixed.UpdateFixedTransactionDTO;


public interface FixedTransactionRestApi {

    /**
     * Creates a fixed transaction.
     *
     * @param fixedTransaction transaction to be inserted
     */
    void createFixedTransaction(CreateFixedTransactionDTO fixedTransaction, RestCallback<FixedTransaction> callback);


    /**
     * Updates a specified transaction.
     *
     * @param transactionId    transaction id that will be updated
     * @param fixedTransaction transaction object with updated information
     */
    void updateFixedTransaction(Long transactionId, UpdateFixedTransactionDTO fixedTransaction, RestCallback<FixedTransaction> callback);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     */

    void deleteFixedTransaction(Long transactionId, RestCallback<Void> callback);

    /**
     * Creates a transaction amount to a given transaction.
     *
     * @param transactionId     id of transaction to add transaction amount to
     * @param transactionAmount transaction amount to be inserted
     */
    void createTransactionAmount(Long transactionId, CreateFixedTransactionAmountDTO transactionAmount,
                                 RestCallback<FixedTransactionAmount> callback);

    /**
     * Updates a transaction amount.
     *
     * @param transactionId       transaction id
     * @param transactionAmountId transaction amount id to be updated
     * @param transactionAmount   updated transaction amount id
     */
    void updateTransactionAmount(Long transactionId, Long transactionAmountId, UpdateFixedTransactionAmountDTO transactionAmount,
                                 RestCallback<FixedTransactionAmount> callback);

    /**
     * Deletes a transaction amount.
     *
     * @param transactionId       transaction id
     * @param transactionAmountId transaction amount id to be deleted
     */
    void deleteTransactionAmount(Long transactionId, Long transactionAmountId, RestCallback<Void> callback);

}

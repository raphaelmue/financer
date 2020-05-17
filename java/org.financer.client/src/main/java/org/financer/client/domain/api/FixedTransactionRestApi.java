package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.domain.model.transaction.FixedTransaction;
import org.financer.client.domain.model.transaction.FixedTransactionAmount;


public interface FixedTransactionRestApi {

    /**
     * Creates a fixed transaction.
     *
     * @param fixedTransaction transaction to be inserted
     */
    ServerRequestHandler createFixedTransaction(FixedTransaction fixedTransaction, RestCallback<FixedTransaction> callback);


    /**
     * Updates a specified transaction.
     *
     * @param fixedTransaction transaction object with updated information
     */
    ServerRequestHandler updateFixedTransaction(FixedTransaction fixedTransaction, RestCallback<FixedTransaction> callback);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     */

    ServerRequestHandler deleteFixedTransaction(Long transactionId, RestCallback<Void> callback);

    /**
     * Creates a transaction amount to a given transaction.
     *
     * @param transactionId     id of transaction to add transaction amount to
     * @param transactionAmount transaction amount to be inserted
     */
    ServerRequestHandler createTransactionAmount(Long transactionId, FixedTransactionAmount transactionAmount,
                                                 RestCallback<FixedTransactionAmount> callback);

    /**
     * Updates a transaction amount.
     *  @param transactionAmountId transaction amount id to be updated
     * @param transactionId       transaction id
     * @param transactionAmount   updated transaction amount id
     */
    ServerRequestHandler updateTransactionAmount(Long transactionId, FixedTransactionAmount transactionAmount,
                                                 RestCallback<FixedTransactionAmount> callback);

    /**
     * Deletes a transaction amount.
     *
     * @param transactionId       transaction id
     * @param transactionAmountId transaction amount id to be deleted
     */
    ServerRequestHandler deleteTransactionAmount(Long transactionId, Long transactionAmountId, RestCallback<Void> callback);

}

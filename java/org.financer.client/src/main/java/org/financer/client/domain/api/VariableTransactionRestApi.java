package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.domain.model.transaction.Product;
import org.financer.client.domain.model.transaction.VariableTransaction;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;

public interface VariableTransactionRestApi {

    /**
     * Creates a variable transaction.
     *
     * @param variableTransaction transaction to be inserted
     */
    void createVariableTransaction(VariableTransaction variableTransaction, RestCallback<VariableTransaction> callback);

    /**
     * Updates a specified transaction.
     *
     * @param transactionId       transaction id that will be updated
     * @param variableTransaction transaction object with updated information
     */
    void updateVariableTransaction(Long transactionId, VariableTransaction variableTransaction, RestCallback<VariableTransaction> callback);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     */
    void deleteVariableTransaction(Long transactionId, RestCallback<Void> callback);

    /**
     * Creates a product.
     *
     * @param transactionId transaction id to which the product will be assigned to
     */
    void createProduct(Long transactionId, String name, Amount amount, Quantity quantity, RestCallback<Product> callback);

    /**
     * Deletes a product.
     *
     * @param transactionId transaction id to which the product is assigned to
     * @param productId     product id to delete
     */
    void deleteProduct(Long transactionId, Long productId, RestCallback<Void> callback);
}

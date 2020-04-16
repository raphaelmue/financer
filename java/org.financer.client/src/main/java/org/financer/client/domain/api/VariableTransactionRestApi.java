package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.domain.model.transaction.Product;
import org.financer.client.domain.model.transaction.VariableTransaction;

public interface VariableTransactionRestApi {

    /**
     * Creates a variable transaction.
     *
     * @param variableTransaction transaction to be inserted
     */
    ServerRequestHandler createVariableTransaction(VariableTransaction variableTransaction, RestCallback<VariableTransaction> callback);

    /**
     * Updates a specified transaction.
     *
     * @param variableTransaction transaction object with updated information
     */
    ServerRequestHandler updateVariableTransaction(VariableTransaction variableTransaction, RestCallback<VariableTransaction> callback);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     */
    ServerRequestHandler deleteVariableTransaction(Long transactionId, RestCallback<Void> callback);

    /**
     * Creates a product.
     *
     * @param transactionId transaction id to which the product will be assigned to
     * @param product
     */
    ServerRequestHandler createProduct(Long transactionId, Product product, RestCallback<Product> callback);

    /**
     * Deletes a product.
     *
     * @param transactionId transaction id to which the product is assigned to
     * @param productId     product id to delete
     */
    ServerRequestHandler deleteProduct(Long transactionId, Long productId, RestCallback<Void> callback);
}

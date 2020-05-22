package org.financer.client.domain.api

import org.financer.client.domain.model.transaction.Product
import org.financer.client.domain.model.transaction.VariableTransaction

interface VariableTransactionRestApi {
    /**
     * Creates a variable transaction.
     *
     * @param variableTransaction transaction to be inserted
     */
    suspend fun createVariableTransaction(variableTransaction: VariableTransaction): VariableTransaction?

    /**
     * Updates a specified transaction.
     *
     * @param variableTransaction transaction object with updated information
     */
    suspend fun updateVariableTransaction(variableTransaction: VariableTransaction): VariableTransaction?

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     */
    suspend fun deleteVariableTransaction(transactionId: Long)

    /**
     * Creates a product.
     *
     * @param transactionId transaction id to which the product will be assigned to
     * @param product
     */
    suspend fun createProduct(transactionId: Long, product: Product): Product?

    /**
     * Deletes a product.
     *
     * @param transactionId transaction id to which the product is assigned to
     * @param productId     product id to delete
     */
    suspend fun deleteProduct(transactionId: Long, productId: Long)
}
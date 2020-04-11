package org.financer.server.application.api;

import org.financer.shared.domain.model.api.transaction.variable.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface VariableTransactionApi {

    /**
     * Creates a variable transaction.
     *
     * @param variableTransaction transaction to be inserted
     * @return transaction object
     */
    @PutMapping(
            value = "/variableTransactions",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<VariableTransactionDTO> createTransaction(@NotNull @Valid @RequestBody CreateVariableTransactionDTO variableTransaction);

    /**
     * Updates a specified transaction.
     *
     * @param transactionId       transaction id that will be updated
     * @param variableTransaction transaction object with updated information
     * @return null
     */
    @PostMapping(
            value = "/variableTransactions/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<VariableTransactionDTO> updateTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                             @NotNull @Valid @RequestBody UpdateVariableTransactionDTO variableTransaction);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     * @return null
     */
    @DeleteMapping(
            value = "/variableTransactions/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId);

    /**
     * Creates a product.
     *
     * @param transactionId transaction id to which the product will be assigned to
     * @param product       product to insert
     * @return product object
     */
    @PutMapping(
            value = "/variableTransactions/{transactionId}/products",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<ProductDTO> createProduct(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                             @NotNull @Valid @RequestBody CreateProductDTO product);

    /**
     * Deletes a product.
     *
     * @param transactionId transaction id to which the product is assigned to
     * @param productId     product id to delete
     * @return void
     */
    @DeleteMapping(
            value = "/variableTransactions/{transactionId}/products/{productId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteProduct(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                       @NotBlank @PathVariable("productId") @Min(1) Long productId);
}

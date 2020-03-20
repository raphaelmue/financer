package org.financer.server.api;

import org.financer.shared.model.transactions.VariableTransaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface VariableTransactionApi {

    /**
     * Updates a specified transaction.
     *
     * @param transactionId       transaction id that will be updated
     * @param variableTransaction transaction object with updated information
     * @return null
     */
    @PostMapping(
            value = "/variableTransaction/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> updateTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                           @NotNull @Valid @RequestParam(value = "transaction") VariableTransaction variableTransaction);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     * @return null
     */
    @DeleteMapping(
            value = "/variableTransaction/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId);
}

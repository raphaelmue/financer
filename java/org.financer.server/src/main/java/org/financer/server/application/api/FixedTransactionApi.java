package org.financer.server.application.api;

import org.financer.shared.domain.model.api.FixedTransactionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface FixedTransactionApi {

    /**
     * Creates a fixed transaction.
     *
     * @param fixedTransaction transaction to be inserted
     * @return transaction object
     */
    @PutMapping(
            value = "/fixedTransaction",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionDTO> createTransaction(@NotNull @Valid @RequestParam(value = "variableTransaction") FixedTransactionDTO fixedTransaction);


    /**
     * Updates a specified transaction.
     *
     * @param transactionId    transaction id that will be updated
     * @param fixedTransaction transaction object with updated information
     * @return null
     */
    @PostMapping(
            value = "/fixedTransaction/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> updateTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                           @NotNull @Valid @RequestParam(value = "transaction") FixedTransactionDTO fixedTransaction);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     * @return null
     */
    @DeleteMapping(
            value = "/fixedTransaction/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId);

}

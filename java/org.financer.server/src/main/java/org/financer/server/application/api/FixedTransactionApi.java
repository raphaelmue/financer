package org.financer.server.application.api;

import org.financer.shared.domain.model.api.transaction.fixed.*;
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
            value = "/fixedTransactions",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionDTO> createFixedTransaction(@NotNull @Valid @RequestBody CreateFixedTransactionDTO fixedTransaction);


    /**
     * Updates a specified transaction.
     *
     * @param transactionId    transaction id that will be updated
     * @param fixedTransaction transaction object with updated information
     * @return null
     */
    @PostMapping(
            value = "/fixedTransactions/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionDTO> updateFixedTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                @NotNull @Valid @RequestParam(value = "fixedTransaction") UpdateFixedTransactionDTO fixedTransaction);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     * @return void
     */
    @DeleteMapping(
            value = "/fixedTransactions/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteFixedTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId);

    /**
     * Creates a transaction amount to a given transaction.
     *
     * @param transactionId     id of transaction to add transaction amount to
     * @param transactionAmount transaction amount to be inserted
     * @return inserted transaction amount
     */
    @PutMapping(
            value = "/fixedTransactions/{transactionId}/transactionAmounts",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionAmountDTO> createTransactionAmount(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                                      @NotNull @Valid @RequestBody CreateFixedTransactionAmountDTO transactionAmount);

    /**
     * Updates a transaction amount.
     *
     * @param transactionId       transaction id
     * @param transactionAmountId transaction amount id to be updated
     * @param transactionAmount   updated transaction amount id
     * @return updated transaction amount
     */
    @PostMapping(
            value = "/fixedTransactions/{transactionId}/transactionAmounts/{transactionAmountId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionAmountDTO> updateTransactionAmount(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                                      @NotBlank @PathVariable("transactionAmountId") @Min(1) Long transactionAmountId,
                                                                      @NotNull @Valid @RequestBody UpdateFixedTransactionAmountDTO transactionAmount);

    /**
     * Deletes a transaction amount.
     *
     * @param transactionId       transaction id
     * @param transactionAmountId transaction amount id to be deleted
     * @return void
     */
    @DeleteMapping(
            value = "/fixedTransactions/{transactionId}/transactionAmounts/{transactionAmountId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteTransactionAmount(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                 @NotBlank @PathVariable("transactionAmountId") @Min(1) Long transactionAmountId);
}

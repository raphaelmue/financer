package org.financer.server.application.api;

import org.financer.shared.domain.model.api.AttachmentDTO;
import org.financer.shared.domain.model.api.VariableTransactionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                           @NotNull @Valid @RequestParam(value = "variableTransaction") VariableTransactionDTO variableTransaction);

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

    /**
     * Inserts a new attachment to the given transaction
     *
     * @param transactionId id of the transaction wo which the attachment will be inserted.
     * @return null
     */
    @PutMapping(
            value = "/variableTransaction/{transactionId}/attachment",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<AttachmentDTO> createAttachment(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                @NotNull @Valid @RequestParam(value = "attachment") AttachmentDTO attachment);

    /**
     * Fetches an attachment with content
     *
     * @param transactionId id of the transaction
     * @param attachmentId  id of the attachment whose content will be returned
     * @return attachment with content
     */
    @GetMapping(
            value = "/variableTransaction/{transactionId}/attachment/{attachmentId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<AttachmentDTO> getAttachment(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                @NotBlank @PathVariable("attachmentId") @Min(1) Long attachmentId);
}

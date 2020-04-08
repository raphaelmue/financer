package org.financer.server.application.api;

import org.financer.shared.domain.model.api.transaction.AttachmentDTO;
import org.financer.shared.domain.model.api.transaction.AttachmentWithContentDTO;
import org.financer.shared.domain.model.api.transaction.CreateAttachmentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface AttachmentApi {

    /**
     * Inserts a new attachment to the given transaction
     *
     * @param transactionId id of the transaction wo which the attachment will be inserted.
     * @return null
     */
    @PutMapping(
            value = {"/variableTransactions/{transactionId}/attachments",
                    "/fixedTransactions/{transactionId}/attachments"},
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<AttachmentDTO> createAttachment(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                   @NotNull @Valid @RequestBody CreateAttachmentDTO attachment);

    /**
     * Fetches an attachment with content
     *
     * @param transactionId id of the transaction
     * @param attachmentId  id of the attachment whose content will be returned
     * @return attachment with content
     */
    @GetMapping(
            value = {"/variableTransactions/{transactionId}/attachments/{attachmentId}",
                    "/fixedTransactions/{transactionId}/attachments/{attachmentId}"},
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<AttachmentWithContentDTO> getAttachment(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                                           @NotBlank @PathVariable("attachmentId") @Min(1) Long attachmentId);

    /**
     * Deletes a specified attachment.
     *
     * @param transactionId transaction id that refers to the attachment that will be deleted
     * @param attachmentId  id of attachment that will be deleted
     * @return null
     */
    @DeleteMapping(
            value = {"/variableTransactions/{transactionId}/attachments/{attachmentId}",
                    "/fixedTransactions/{transactionId}/attachments/{attachmentId}"},
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteTransaction(@NotBlank @PathVariable("transactionId") @Min(1) Long transactionId,
                                           @NotBlank @PathVariable("attachmentId") @Min(1) Long attachmentId);

}

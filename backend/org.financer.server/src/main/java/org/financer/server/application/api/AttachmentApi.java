package org.financer.server.application.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.financer.shared.domain.model.api.transaction.AttachmentDTO;
import org.financer.shared.domain.model.api.transaction.AttachmentWithContentDTO;
import org.financer.shared.domain.model.api.transaction.CreateAttachmentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Tag(name = "attachment", description = "Operations with attachments")
public interface AttachmentApi {

    /**
     * Inserts a new attachment to the given transaction
     *
     * @param transactionId id of the transaction wo which the attachment will be inserted.
     * @return null
     */
    @Operation(
            summary = "Creates a new attachment",
            tags = {"transaction", "attachment"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "201",
            description = "Attachment was successfully created.",
            content = @Content(schema = @Schema(implementation = AttachmentDTO.class)))
    @PutMapping(
            value = {"/variableTransactions/{transactionId}/attachments",
                    "/fixedTransactions/{transactionId}/attachments"},
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<AttachmentDTO> createAttachment(
            @Parameter(description = "ID of the transaction to which the attachment is added", required = true)
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Attachment to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateAttachmentDTO.class)))
            @RequestBody @NotNull @Valid CreateAttachmentDTO attachment);

    /**
     * Fetches an attachment with content
     *
     * @param transactionId id of the transaction
     * @param attachmentId  id of the attachment whose content will be returned
     * @return attachment with content
     */
    @Operation(
            summary = "Fetches an attachment with content",
            tags = {"transaction", "attachment"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "successful operation",
            content = @Content(schema = @Schema(implementation = AttachmentWithContentDTO.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Attachment ID was not found.")
    @GetMapping(
            value = {"/variableTransactions/{transactionId}/attachments/{attachmentId}",
                    "/fixedTransactions/{transactionId}/attachments/{attachmentId}"},
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<AttachmentWithContentDTO> getAttachment(
            @Parameter(description = "ID of the transaction to which the attachment belongs", required = true)
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @Parameter(description = "ID of the attachment to return", required = true)
            @NotBlank @PathVariable("attachmentId") @Min(1) Long attachmentId);

    /**
     * Deletes a specified attachment.
     *
     * @param transactionId transaction id that refers to the attachment that will be deleted
     * @param attachmentId  id of attachment that will be deleted
     * @return null
     */
    @Operation(
            summary = "Fetches an attachment with content",
            tags = {"transaction", "attachment"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Attachment was successfully deleted")
    @DeleteMapping(
            value = {"/variableTransactions/{transactionId}/attachments/{attachmentId}",
                    "/fixedTransactions/{transactionId}/attachments/{attachmentId}"},
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "ID of the transaction to which the attachment belongs", required = true)
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @Parameter(description = "ID of the attachment to be deleted", required = true)
            @NotBlank @PathVariable("attachmentId") @Min(1) Long attachmentId);

}

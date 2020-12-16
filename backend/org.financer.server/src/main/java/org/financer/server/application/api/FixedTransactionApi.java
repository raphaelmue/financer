package org.financer.server.application.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.financer.shared.domain.model.api.transaction.fixed.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Tag(name = "fixed-transaction", description = "Operations with fixed transactions")
@RequestMapping("/fixedTransactions")
public interface FixedTransactionApi {


    /**
     * Creates a fixed transaction.
     *
     * @param fixedTransaction transaction to be inserted
     * @return transaction object
     */
    @Operation(
            summary = "Creates a new fixed transaction",
            tags = {"fixed-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Fixed transaction was successfully created.")
    @PutMapping(
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionDTO> createFixedTransaction(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fixed transaction that will be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateFixedTransactionDTO.class)))
            @RequestBody @Valid CreateFixedTransactionDTO fixedTransaction);

    /**
     * Fetches a fixed transaction by id.
     *
     * @param transactionId transaction id to be fetched
     * @return transaction object
     */
    @Operation(
            operationId = "getFixedTransactionById",
            summary = "Fetches a fixed transaction by id",
            tags = {"fixed-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Fixed transaction was successfully fetched.",
            content = @Content(schema = @Schema(implementation = FixedTransactionDTO.class)))
    @GetMapping(
            value = "/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionDTO> getFixedTransactionById(
            @Parameter(description = "ID of the transaction that will be fetched")
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId);

    /**
     * Updates a specified transaction.
     *
     * @param transactionId    transaction id that will be updated
     * @param fixedTransaction transaction object with updated information
     * @return null
     */
    @Operation(
            summary = "Updates a fixed transaction",
            tags = {"fixed-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Fixed transaction was successfully updated.",
            content = @Content(schema = @Schema(implementation = FixedTransactionDTO.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Fixed transaction ID was not found.")
    @PostMapping(
            value = "/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionDTO> updateFixedTransaction(
            @Parameter(description = "ID of the transaction to be updated", required = true)
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fixed transaction that will be updated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateFixedTransactionDTO.class)))
            @RequestBody @NotNull @Valid UpdateFixedTransactionDTO fixedTransaction);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     * @return void
     */
    @Operation(
            summary = "Deletes a fixed transaction",
            tags = {"fixed-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Fixed transaction was successfully deleted.")
    @DeleteMapping(
            value = "/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteFixedTransaction(
            @Parameter(description = "ID of the transaction to be updated", required = true)
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId);

    /**
     * Creates a transaction amount to a given transaction.
     *
     * @param transactionId     id of transaction to add transaction amount to
     * @param transactionAmount transaction amount to be inserted
     * @return inserted transaction amount
     */
    @Operation(
            summary = "Creates a new fixed transaction amount",
            tags = {"fixed-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Fixed transaction amount was successfully created.",
            content = @Content(schema = @Schema(implementation = FixedTransactionAmountDTO.class)))
    @PutMapping(
            value = "/{transactionId}/transactionAmounts",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionAmountDTO> createFixedTransactionAmount(
            @Parameter(description = "ID of the transaction that is assigned to the transaction amoutn", required = true)
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fixed transaction amount that will be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateFixedTransactionAmountDTO.class)))
            @RequestBody @NotNull @Valid CreateFixedTransactionAmountDTO transactionAmount);

    /**
     * Updates a transaction amount.
     *
     * @param transactionId       transaction id
     * @param transactionAmountId transaction amount id to be updated
     * @param transactionAmount   updated transaction amount id
     * @return updated transaction amount
     */
    @Operation(
            summary = "Updates a fixed transaction amount",
            tags = {"fixed-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Fixed transaction amount was successfully updated.",
            content = @Content(schema = @Schema(implementation = FixedTransactionAmountDTO.class)))
    @PostMapping(
            value = "/{transactionId}/transactionAmounts/{transactionAmountId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<FixedTransactionAmountDTO> updateTransactionAmount(
            @Parameter(description = "ID of the transaction that is assigned to the transaction amount", required = true)
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @Parameter(description = "ID of the transaction amount to be updated")
            @PathVariable("transactionAmountId") @NotBlank @Min(1) Long transactionAmountId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fixed transaction amount that will be updated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateFixedTransactionAmountDTO.class)))
            @RequestBody @NotNull @Valid UpdateFixedTransactionAmountDTO transactionAmount);

    /**
     * Deletes a transaction amount.
     *
     * @param transactionId       transaction id
     * @param transactionAmountId transaction amount id to be deleted
     * @return void
     */
    @Operation(
            summary = "Deletes a fixed transaction amount",
            tags = {"fixed-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Fixed transaction amount was successfully deleted.")
    @DeleteMapping(
            value = "/{transactionId}/transactionAmounts/{transactionAmountId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteTransactionAmount(
            @Parameter(description = "ID of the transaction that is assigned to the transaction amount", required = true)
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @Parameter(description = "ID of the transaction amount to be deleted")
            @PathVariable("transactionAmountId") @NotBlank @Min(1) Long transactionAmountId);
}

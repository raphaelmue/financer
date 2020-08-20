package org.financer.server.application.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.financer.shared.domain.model.api.transaction.variable.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Tag(name = "variable-transaction", description = "Operations with variable transactions")
public interface VariableTransactionApi {

    /**
     * Creates a variable transaction.
     *
     * @param variableTransaction transaction to be inserted
     * @return transaction object
     */
    @Operation(
            summary = "Creates a new variable transaction",
            tags = {"variable-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "201",
            description = "Variable transaction was successfully created.",
            content = @Content(schema = @Schema(implementation = VariableTransactionDTO.class)))
    @PutMapping(
            value = "/variableTransactions",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<VariableTransactionDTO> createTransaction(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Variable transaction that will be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateVariableTransactionDTO.class)))
            @RequestBody @NotNull @Valid CreateVariableTransactionDTO variableTransaction);

    /**
     * Updates a specified transaction.
     *
     * @param transactionId       transaction id that will be updated
     * @param variableTransaction transaction object with updated information
     * @return null
     */
    @Operation(
            summary = "Updates a variable transaction",
            tags = {"variable-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Variable transaction was successfully updated.",
            content = @Content(schema = @Schema(implementation = VariableTransactionDTO.class)))
    @PostMapping(
            value = "/variableTransactions/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<VariableTransactionDTO> updateTransaction(
            @Parameter(description = "ID of the transaction that will be updated")
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Variable transaction that will be updated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateVariableTransactionDTO.class)))
            @RequestBody @NotNull @Valid UpdateVariableTransactionDTO variableTransaction);

    /**
     * Deletes a specified transaction.
     *
     * @param transactionId transaction id that refers to the transaction that will be deleted
     * @return null
     */
    @Operation(
            summary = "Deletes a variable transaction",
            tags = {"variable-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Variable transaction was successfully deleted.")
    @DeleteMapping(
            value = "/variableTransactions/{transactionId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "ID of the transaction that will be deleted")
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId);

    /**
     * Creates a product.
     *
     * @param transactionId transaction id to which the product will be assigned to
     * @param product       product to insert
     * @return product object
     */
    @Operation(
            summary = "Creates a new product",
            tags = {"variable-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "201",
            description = "Product was successfully created.",
            content = @Content(schema = @Schema(implementation = ProductDTO.class)))
    @PutMapping(
            value = "/variableTransactions/{transactionId}/products",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<ProductDTO> createProduct(
            @Parameter(description = "ID of the transaction that is assigned to the new product")
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product that will be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateProductDTO.class)))
            @RequestBody @NotNull @Valid CreateProductDTO product);

    /**
     * Deletes a product.
     *
     * @param transactionId transaction id to which the product is assigned to
     * @param productId     product id to delete
     * @return void
     */
    @Operation(
            summary = "Deletes a product",
            tags = {"variable-transaction", "transaction"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Product was successfully deleted.")
    @DeleteMapping(
            value = "/variableTransactions/{transactionId}/products/{productId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID of the transaction that is assigned to the product")
            @PathVariable("transactionId") @NotBlank @Min(1) Long transactionId,
            @Parameter(description = "ID of the product that will be deleted")
            @PathVariable("productId") @NotBlank @Min(1) Long productId);
}
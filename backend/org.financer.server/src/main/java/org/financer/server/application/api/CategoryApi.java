package org.financer.server.application.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.category.CreateCategoryDTO;
import org.financer.shared.domain.model.api.category.UpdateCategoryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Tag(name = "category", description = "Operations with categories")
public interface CategoryApi {

    /**
     * Creates a new category.
     *
     * @param category category object that will be inserted
     * @return Category object
     */
    @Operation(
            summary = "Creates a new category",
            tags = {"category"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "201",
            description = "Category was successfully created.",
            content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    @PutMapping(
            value = "/categories",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<CategoryDTO> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category to be created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCategoryDTO.class)))
                @RequestBody @NotNull @Valid CreateCategoryDTO category);

    /**
     * Updates a specified category.
     *
     * @param categoryId category id that will be updated
     * @param category   category object with updated information
     * @return update category
     */
    @Operation(
            summary = "Updates a category",
            tags = {"category"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Category was successfully updated.",
            content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Category ID was not found")
    @PostMapping(
            value = "/categories/{categoryId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "ID of the category that will be updated", required = true)
            @PathVariable("categoryId") @NotBlank @Min(1) Long categoryId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category to be updated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCategoryDTO.class)))
            @RequestBody @NotNull @Valid UpdateCategoryDTO category);

    /**
     * Deletes a specified category.
     *
     * @param categoryId category id that refers to the category that will be deleted
     * @return null
     */
    @Operation(
            summary = "Deletes a category",
            tags = {"category"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Category was successfully deleted.")
    @DeleteMapping(
            value = "/categories/{categoryId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID of the category that will be deleted", required = true)
            @PathVariable("categoryId") @NotBlank @Min(1) Long categoryId);

}

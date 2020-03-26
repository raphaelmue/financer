package org.financer.server.application.api;

import org.financer.shared.domain.model.api.CategoryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface CategoryApi {

    /**
     * Creates a new category.
     *
     * @param category category object that will be inserted
     * @return Category object
     */
    @PutMapping(
            value = "/category",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<CategoryDTO> createCategory(@NotNull @Valid @RequestParam(value = "category") CategoryDTO category);

    /**
     * Updates a specified category.
     *
     * @param categoryId category id that will be updated
     * @param category   category object with updated information
     * @return null
     */
    @PostMapping(
            value = "/category/{categoryId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> updateCategory(@NotBlank @PathVariable("categoryId") @Min(1) Long categoryId,
                                        @NotNull @Valid @RequestParam(value = "category") CategoryDTO category);

    /**
     * Deletes a specified category.
     *
     * @param categoryId category id that refers to the category that will be deleted
     * @return null
     */
    @DeleteMapping(
            value = "/category/{categoryId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteCategory(@NotBlank @PathVariable("categoryId") @Min(1) Long categoryId);

}

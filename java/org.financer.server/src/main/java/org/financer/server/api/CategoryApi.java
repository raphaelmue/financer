package org.financer.server.api;

import org.financer.shared.model.categories.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface CategoryApi {

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
                                        @NotNull @Valid @RequestParam(value = "category") Category category);

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

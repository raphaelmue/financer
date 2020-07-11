package org.financer.client.domain.api

import org.financer.client.domain.model.category.Category

interface CategoryRestApi {
    /**
     * Creates a new category.
     *
     * @param category category object that will be inserted
     */
    suspend fun createCategory(category: Category): Category?

    /**
     * Updates a specified category.
     *
     * @param category
     */
    suspend fun updateCategory(category: Category): Category?

    /**
     * Deletes a specified category.
     *
     * @param categoryId category id that refers to the category that will be deleted
     */
    suspend fun deleteCategory(categoryId: Long)
}
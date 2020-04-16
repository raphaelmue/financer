package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.domain.model.category.Category;

public interface CategoryRestApi {

    /**
     * Creates a new category.
     *
     * @param category category object that will be inserted
     */
    ServerRequestHandler createCategory(Category category, RestCallback<Category> callback);

    /**
     * Updates a specified category.
     *
     * @param category
     */
    ServerRequestHandler updateCategory(Category category, RestCallback<Category> callback);

    /**
     * Deletes a specified category.
     *
     * @param categoryId category id that refers to the category that will be deleted
     */
    ServerRequestHandler deleteCategory(Long categoryId, RestCallback<Void> callback);

}

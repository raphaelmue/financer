package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.domain.model.category.Category;
import org.financer.shared.domain.model.api.category.CreateCategoryDTO;
import org.financer.shared.domain.model.value.objects.CategoryClass;

public interface CategoryRestApi {

    /**
     * Creates a new category.
     *
     * @param category category object that will be inserted
     */
    void createCategory(CreateCategoryDTO category, RestCallback<Category> callback);

    /**
     * Updates a specified category.
     *
     * @param categoryId category id that will be updated
     */
    void updateCategory(Long categoryId, String name, CategoryClass.Values categoryClass, long parentId,
                        RestCallback<Category> callback);

    /**
     * Deletes a specified category.
     *
     * @param categoryId category id that refers to the category that will be deleted
     */
    void deleteCategory(Long categoryId, RestCallback<Void> callback);

}

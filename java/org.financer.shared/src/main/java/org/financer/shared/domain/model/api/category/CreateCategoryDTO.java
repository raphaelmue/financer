package org.financer.shared.domain.model.api.category;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.CategoryClass;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO for creating a new category.
 */
public class CreateCategoryDTO implements DataTransferObject {

    @NotNull
    @Size(min = 1, max = 64)
    private String name;

    @NotNull
    private CategoryClass.Values categoryClass;

    private long parentId;

    public String getName() {
        return name;
    }

    public CreateCategoryDTO setName(String name) {
        this.name = name;
        return this;
    }

    public CategoryClass.Values getCategoryClass() {
        return categoryClass;
    }

    public CreateCategoryDTO setCategoryClass(CategoryClass.Values categoryClass) {
        this.categoryClass = categoryClass;
        return this;
    }

    public long getParentId() {
        return parentId;
    }

    public CreateCategoryDTO setParentId(long parentId) {
        this.parentId = parentId;
        return this;
    }
}

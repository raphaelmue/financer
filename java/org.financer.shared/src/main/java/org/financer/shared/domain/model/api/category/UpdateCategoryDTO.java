package org.financer.shared.domain.model.api.category;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.CategoryClass;

import javax.validation.constraints.Size;

public class UpdateCategoryDTO implements DataTransferObject {

    @Size(min = 1, max = 64)
    private String name = null;

    private CategoryClass.Values categoryClass = null;

    private long parentId = -1;

    public String getName() {
        return name;
    }

    public UpdateCategoryDTO setName(String name) {
        this.name = name;
        return this;
    }

    public CategoryClass.Values getCategoryClass() {
        return categoryClass;
    }

    public UpdateCategoryDTO setCategoryClass(CategoryClass.Values categoryClass) {
        this.categoryClass = categoryClass;
        return this;
    }

    public long getParentId() {
        return parentId;
    }

    public UpdateCategoryDTO setParentId(long parentId) {
        this.parentId = parentId;
        return this;
    }
}

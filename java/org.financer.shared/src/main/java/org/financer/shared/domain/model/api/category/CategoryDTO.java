package org.financer.shared.domain.model.api.category;

import org.financer.shared.domain.model.value.objects.CategoryClass;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class CategoryDTO {

    @NotNull
    @Size(min = 1)
    private int id;

    @NotNull
    private CategoryClass.Values categoryClass;

    @NotNull
    private String name;

    private Set<CategoryDTO> children;

    public int getId() {
        return id;
    }

    public CategoryDTO setId(int id) {
        this.id = id;
        return this;
    }

    public CategoryClass.Values getCategoryClass() {
        return categoryClass;
    }

    public CategoryDTO setCategoryClass(CategoryClass.Values categoryClass) {
        this.categoryClass = categoryClass;
        return this;
    }

    public String getName() {
        return name;
    }

    public CategoryDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Set<CategoryDTO> getChildren() {
        return children;
    }

    public CategoryDTO setChildren(Set<CategoryDTO> children) {
        this.children = children;
        return this;
    }

}

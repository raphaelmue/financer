package org.financer.shared.domain.model.api.category;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.CategoryClass;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Schema(name = "Category", description = "Schema for a category")
public class CategoryDTO implements DataTransferObject {

    @NotNull
    @Size(min = 1)
    @Schema(description = "Identifier of the category", required = true, minimum = "1")
    private int id;

    @NotNull
    @Schema(description = "Category class of the category", required = true, enumAsRef = true)
    private CategoryClass.Values categoryClass;

    @NotNull
    @Schema(description = "Name of the category", required = true, example = "Food")
    private String name;

    @Schema(description = "List of the child categories")
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

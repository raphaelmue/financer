package org.financer.shared.domain.model.api;

import com.google.gson.annotations.SerializedName;
import org.financer.shared.domain.model.value.objects.CategoryClass;

import java.util.List;

// @Validated
public class CategoryDTO {

    @SerializedName("id")
    // @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @SerializedName("categoryClass")
    // @ApiModelProperty(value = "Category Class", required = true, example = "fixedRevenue")
    private CategoryClass.Values categoryClass;

    @SerializedName("name")
    // @ApiModelProperty(value = "Name", required = true, example = "Food")
    private String name;

    @SerializedName("children")
    // @ApiModelProperty(value = "List of children categories", example = "Food")
    private List<CategoryDTO> children;

    @SerializedName("variableTransactions")
    // @ApiModelProperty(value = "Identifier")
    private List<VariableTransactionDTO> variableTransactions;

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

    public List<CategoryDTO> getChildren() {
        return children;
    }

    public CategoryDTO setChildren(List<CategoryDTO> children) {
        this.children = children;
        return this;
    }

    public List<VariableTransactionDTO> getVariableTransactions() {
        return variableTransactions;
    }

    public CategoryDTO setVariableTransactions(List<VariableTransactionDTO> variableTransactions) {
        this.variableTransactions = variableTransactions;
        return this;
    }
}

package org.financer.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.financer.shared.model.categories.BaseCategory;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public class CategoryDTO {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @JsonProperty("categoryClass")
    @ApiModelProperty(value = "Category Class", required = true, example = "fixedRevenue")
    private BaseCategory.CategoryClass categoryClass;

    @JsonProperty("name")
    @ApiModelProperty(value = "Name", required = true, example = "Food")
    private String name;

    @JsonProperty("children")
    @ApiModelProperty(value = "List of children categories", example = "Food")
    private List<CategoryDTO> children;

    @JsonProperty("variableTransactions")
    @ApiModelProperty(value = "Identifier")
    private List<VariableTransactionDTO> variableTransactions;

    public int getId() {
        return id;
    }

    public CategoryDTO setId(int id) {
        this.id = id;
        return this;
    }

    public BaseCategory.CategoryClass getCategoryClass() {
        return categoryClass;
    }

    public CategoryDTO setCategoryClass(BaseCategory.CategoryClass categoryClass) {
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

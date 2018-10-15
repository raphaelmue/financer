package de.raphaelmuesseler.financer.shared.model;

import de.raphaelmuesseler.financer.util.collections.Tree;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CategoryTree implements Serializable, AmountProvider, Tree<Category> {
    private static final long serialVersionUID = -5848321222290793608L;

    private final Category category;
    private final List<CategoryTree> children = new ArrayList<>();
    private final BaseCategory.CategoryClass categoryClass;
    private CategoryTree parent;

    public CategoryTree(BaseCategory.CategoryClass categoryClass, Category category) {
        this(categoryClass, null, category);
    }

    public CategoryTree(BaseCategory.CategoryClass categoryClass, CategoryTree parent, Category category) {
        this.categoryClass = categoryClass;
        this.parent = parent;
        this.category = category;
    }

    @Override
    public double getAmount() {
        double amount = 0;

        for (AmountProvider amountProvider : this.children) {
            amount += amountProvider.getAmount();
        }

        return amount;
    }

    @Override
    public double getAmount(LocalDate localDate) {
        double amount = 0;

        for (AmountProvider amountProvider : this.children) {
            amount += amountProvider.getAmount(localDate);
        }

        return amount;
    }

    /**
     * GETTER
     */

    public BaseCategory.CategoryClass getCategoryClass() {
        return categoryClass;
    }

    @Override
    public Category getValue() {
        return this.category;
    }

    @Override
    public Tree<Category> getParent() {
        return this.parent;
    }

    @Override
    public List<? extends Tree<Category>> getChildren() {
        return this.children;
    }

    /**
     * SETTER
     */

    @Override
    public void setParent(Tree<Category> parent) {
        this.parent = (CategoryTree) parent;
    }

    @Override
    public String toString() {
        return this.getValue().toString();
    }
}


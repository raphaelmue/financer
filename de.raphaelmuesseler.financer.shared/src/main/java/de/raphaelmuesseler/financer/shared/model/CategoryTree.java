package de.raphaelmuesseler.financer.shared.model;

import de.raphaelmuesseler.financer.util.collections.Tree;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CategoryTree implements Serializable, AmountProvider, Tree<CategoryTree.Category> {
    private static final long serialVersionUID = -5848321222290793608L;

    private final Category category;
    private final List<CategoryTree> children = new ArrayList<>();
    private final BaseCategory.CategoryClass categoryClass;
    private final CategoryTree parent;

    public CategoryTree(BaseCategory.CategoryClass categoryClass, String name) {
        this(categoryClass, null, -1, name);
    }

    public CategoryTree(BaseCategory.CategoryClass categoryClass, int id, String name) {
        this(categoryClass, null, id, name);
    }

    public CategoryTree(BaseCategory.CategoryClass categoryClass, CategoryTree parent, int id, String name) {
        this.categoryClass = categoryClass;
        this.parent = parent;
        this.category = new Category(id, name);
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
    public Category getCategory() {
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

    public class Category {
        private final int id;
        private final String name;
        private String prefix = null;

        public Category(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }
}


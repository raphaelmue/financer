package de.raphaelmuesseler.financer.shared.model;

import de.raphaelmuesseler.financer.shared.model.transactions.AbstractTransaction;
import de.raphaelmuesseler.financer.util.collections.Tree;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryTree implements Serializable, AmountProvider, Tree<Category> {
    private static final long serialVersionUID = -5848321222290793608L;

    private final Category category;
    private final List<CategoryTree> children = new ArrayList<>();
    private Set<AbstractTransaction> transactions = new HashSet<>();
    private BaseCategory.CategoryClass categoryClass;
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

        if (this.isLeaf()) {
            for (AmountProvider amountProvider : this.transactions) {
                amount += amountProvider.getAmount();
            }
        } else {
            for (AmountProvider amountProvider : this.children) {
                amount += amountProvider.getAmount();
            }
        }

        return amount;
    }

    @Override
    public double getAmount(LocalDate localDate) {
        double amount = 0;

        if (this.isLeaf()) {
            if (this.transactions != null) {
                for (AmountProvider amountProvider : this.transactions) {
                    amount += amountProvider.getAmount(localDate);
                }
            }
        } else {
            for (AmountProvider amountProvider : this.children) {
                amount += amountProvider.getAmount(localDate);
            }
        }

        return amount;
    }

    @Override
    public double getAmount(LocalDate startDate, LocalDate endDate) {
        double amount = 0;

        if (this.isLeaf()) {
            if (this.transactions != null) {
                for (AmountProvider amountProvider : this.transactions) {
                    amount += amountProvider.getAmount(startDate, endDate);
                }
            }
        } else {
            for (AmountProvider amountProvider : this.children) {
                amount += amountProvider.getAmount(startDate, endDate);
            }
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
    public CategoryTree getParent() {
        return this.parent;
    }

    @Override
    public List<CategoryTree> getChildren() {
        return this.children;
    }

    public Set<AbstractTransaction> getTransactions() {
        if (this.transactions == null) {
            this.transactions = new HashSet<>();
        }
        return transactions;
    }

    /**
     * SETTER
     */

    @Override
    public void setParent(Tree<Category> parent) {
        this.parent = (CategoryTree) parent;
    }

    public void setCategoryClass(BaseCategory.CategoryClass categoryClass) {
        this.categoryClass = categoryClass;
    }

    @Override
    public String toString() {
        return this.getValue().toString();
    }
}


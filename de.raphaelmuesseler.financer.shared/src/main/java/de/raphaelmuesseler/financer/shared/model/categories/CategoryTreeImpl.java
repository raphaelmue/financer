package de.raphaelmuesseler.financer.shared.model.categories;

import de.raphaelmuesseler.financer.shared.model.transactions.AmountProvider;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.Tree;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CategoryTreeImpl implements CategoryTree {
    private static final long serialVersionUID = -5848321222290793608L;

    private final Category category;
    private final List<CategoryTree> children = new ArrayList<>();
    private HashSet<Transaction> transactions = new HashSet<>();
    private CategoryTree parent;

    public CategoryTreeImpl(Category category) {
        this(null, category);
    }

    public CategoryTreeImpl(CategoryTree parent, Category category) {
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

    public HashSet<Transaction> getTransactions() {
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

    @Override
    public String toString() {
        return this.getValue().toString();
    }
}


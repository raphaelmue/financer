package org.financer.client.domain.model.category;

import org.financer.client.domain.model.transaction.Transaction;
import org.financer.client.domain.model.user.User;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.Formattable;
import org.financer.shared.domain.model.Settings;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.financer.util.collections.Tree;

import java.io.Serializable;
import java.util.*;

public class Category implements Serializable, Tree, AmountProvider, Formattable {
    private static final long serialVersionUID = 5491420625985358596L;

    private long id;
    private User user;
    private CategoryClass categoryClass;
    private Category parent;
    private String name;
    private Set<Category> children;
    private Set<Transaction> transactions = new HashSet<>();

    public String getPrefix() {
        if (this.isRoot()) {
            return Integer.toString(categoryClass.getCategoryClass().ordinal() + 1);
        }

        List<Category> neighbors = new ArrayList<>(parent.getChildren());
        neighbors.sort(Comparator.comparing(Category::getName));
        final int index = neighbors.indexOf(this) + 1;

        return parent.getPrefix() + "." + index;
    }

    @Override
    public Amount getAmount() {
        Amount amount = new Amount();

        for (AmountProvider amountProvider : this.transactions) {
            amount = amount.add(amountProvider.getAmount());
        }
        if (this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getAmount());
            }
        }

        return amount;
    }

    @Override
    public Amount getAmount(ValueDate valueDate) {
        Amount amount = new Amount();

        for (AmountProvider amountProvider : this.transactions) {
            amount = amount.add(amountProvider.getAmount(valueDate));
        }
        if (this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getAmount(valueDate));
            }
        }

        return amount;
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        Amount amount = new Amount();

        for (AmountProvider amountProvider : this.transactions) {
            amount = amount.add(amountProvider.getAmount(timeRange));
        }
        if (!this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getAmount(timeRange));
            }
        }

        return amount;
    }

    @Override
    public boolean isFixed() {
        return this.categoryClass.isFixed();
    }

    @Override
    public boolean isRevenue() {
        return this.categoryClass.isRevenue();
    }

    @Override
    public void adjustAmountSign() {
        for (AmountProvider amountProvider : this.transactions) {
            amountProvider.adjustAmountSign();
        }
        if (!this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amountProvider.adjustAmountSign();
            }
        }
    }

    @Override
    public String format(Settings settings) {
        final String prefix = this.getPrefix();
        if (!prefix.isBlank()) {
            return this.getPrefix() + " " + this.getName();
        }
        return this.name;
    }

    /*
     * Getters and Setters
     */

    public long getId() {
        return id;
    }

    public Category setId(long id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Category setUser(User user) {
        this.user = user;
        return this;
    }

    public CategoryClass getCategoryClass() {
        return categoryClass;
    }

    public Category setCategoryClass(CategoryClass categoryClass) {
        this.categoryClass = categoryClass;
        return this;
    }

    @Override
    public Category getParent() {
        return parent;
    }

    @Override
    public Category setParent(Tree parent) {
        this.parent = (Category) parent;
        return this;
    }


    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Set<Category> getChildren() {
        return children;
    }

    public Category setChildren(Set<Category> children) {
        this.children = children;
        return this;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category[" + "id=" + id + ", categoryClass=" + categoryClass + ", name='" + name + '\'' + ']';
    }
}

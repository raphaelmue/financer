package de.raphaelmuesseler.financer.shared.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable, AmountProvider {
    private static final long serialVersionUID = -5848321222290793608L;

    private int id;
    private final String name;
    private String prefix = null;

    private final List<AmountProvider> children = new ArrayList<>();
    private final BaseCategory.CategoryClass categoryClass;
    private final AmountProvider parent;

    public Category(BaseCategory.CategoryClass categoryClass, String name) {
        this(categoryClass, null, -1, name);
    }

    public Category(BaseCategory.CategoryClass categoryClass, int id, String name) {
        this(categoryClass, null, id, name);
    }

    public Category(BaseCategory.CategoryClass categoryClass, AmountProvider parent, int id, String name) {
        this.categoryClass = categoryClass;
        this.parent = parent;
        this.id = id;
        this.name = name;
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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public BaseCategory.CategoryClass getCategoryClass() {
        return categoryClass;
    }

    public AmountProvider getParent() {
        return parent;
    }

    public List<AmountProvider> getChildren() {
        return children;
    }

    /**
     * SETTER
     */

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Category && this.getId() == ((Category) obj).getId());
    }
}


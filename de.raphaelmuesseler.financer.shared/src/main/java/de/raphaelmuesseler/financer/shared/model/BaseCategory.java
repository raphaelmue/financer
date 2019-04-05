package de.raphaelmuesseler.financer.shared.model;

import de.raphaelmuesseler.financer.shared.model.transactions.AbstractTransaction;
import de.raphaelmuesseler.financer.util.collections.Action;
import de.raphaelmuesseler.financer.util.collections.Tree;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class BaseCategory implements Serializable, CategoryTree {
    public enum CategoryClass {
        FIXED_REVENUE(0, "fixedRevenue"),
        VARIABLE_REVENUE(1, "variableRevenue"),
        FIXED_EXPENSES(2, "fixedExpenses"),
        VARIABLE_EXPENSES(3, "variableExpenses");

        private final int index;

        private final String name;
        CategoryClass(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public static CategoryClass getCategoryClassByName(String name) {
            for (CategoryClass categoryClass : values()) {
                if (categoryClass.getName().equals(name)) {
                    return categoryClass;
                }
            }
            return null;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public static CategoryClass getCategoryClassByIndex(int index) {
            for (CategoryClass categoryClass : values()) {
                if (categoryClass.getIndex() == index) {
                    return categoryClass;
                }
            }
            return null;
        }

        public boolean isFixed() {
            return (this == FIXED_EXPENSES || this == FIXED_REVENUE);
        }

        public boolean isRevenue() {
            return (this == VARIABLE_REVENUE || this == FIXED_REVENUE);
        }

    }
    private static final long serialVersionUID = 6444376234610401363L;

    private final Map<CategoryClass, CategoryTreeImpl> categories;
    private final Category value = new Category(-1, "root", -1, -1);

    public BaseCategory() {
        this.categories = new HashMap<>(4);
        this.value.setPrefix("0.");

        for (CategoryClass categoryClass : CategoryClass.values()) {
            this.categories.put(categoryClass, new CategoryTreeImpl(categoryClass, this, new Category(-1, categoryClass.getName(), -1, -1)));
        }
    }

    @Override
    public Category getValue() {
        return this.value;
    }

    @Override
    public Tree<Category> getParent() {
        return null;
    }

    @Override
    public CategoryClass getCategoryClass() {
        return null;
    }

    @Override
    public Set<AbstractTransaction> getTransactions() {
        return new HashSet<>();
    }

    @Override
    public void setParent(Tree<Category> parent) {

    }

    @Override
    public List<CategoryTree> getChildren() {
        List<CategoryTree> result = new ArrayList<>();
        for (CategoryClass categoryClass : CategoryClass.values()) {
            result.add(this.categories.get(categoryClass));
        }
        return result;
    }


    public CategoryTreeImpl getCategoryTreeByCategoryClass(CategoryClass categoryClass) {
        return this.categories.get(categoryClass);
    }

    public void traverse(Action<Tree<Category>> action) {
        for (CategoryTree categoryTree : this.categories.values()) {
            TreeUtil.traverse(categoryTree, action);
        }
    }

    @Override
    public double getAmount() {
        double amount = 0;

        for (AmountProvider amountProvider : this.getChildren()) {
            amount += amountProvider.getAmount();
        }

        return amount;
    }

    @Override
    public double getAmount(LocalDate localDate) {
        double amount = 0;

        for (AmountProvider amountProvider : this.getChildren()) {
            amount += amountProvider.getAmount(localDate);
        }

        return amount;
    }

    @Override
    public double getAmount(LocalDate startDate, LocalDate endDate) {
        double amount = 0;

        for (CategoryTree amountProvider : this.getChildren()) {
            amount += amountProvider.getAmount(startDate, endDate);
        }

        return amount;
    }

    @Override
    public void setCategoryClass(CategoryClass categoryClass) {
        throw new IllegalArgumentException("BaseCategory must not have a category class");
    }
}

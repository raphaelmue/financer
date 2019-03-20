package de.raphaelmuesseler.financer.shared.model;

import de.raphaelmuesseler.financer.util.collections.Action;
import de.raphaelmuesseler.financer.util.collections.Tree;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseCategory implements Serializable, AmountProvider, Tree<Category> {
    private static final long serialVersionUID = 6444376234610401363L;

    private final Map<CategoryClass, CategoryTree> categories;

    @Override
    public Category getValue() {
        return null;
    }

    @Override
    public Tree<Category> getParent() {
        return null;
    }

    @Override
    public void setParent(Tree<Category> parent) {

    }

    @Override
    public List<Tree<Category>> getChildren() {
        List<Tree<Category>> result = new ArrayList<>();
        for (CategoryClass categoryClass : CategoryClass.values()) {
            result.add(this.categories.get(categoryClass));
        }
        return result;
    }

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


    public BaseCategory() {
        this.categories = new HashMap<>(4);

        for (CategoryClass categoryClass : CategoryClass.values()) {
            this.categories.put(categoryClass, new CategoryTree(categoryClass, null, new Category(-1, categoryClass.getName(), -1, -1)));
        }
    }

    public CategoryTree getCategoryTreeByCategoryClass(CategoryClass categoryClass) {
        return this.categories.get(categoryClass);
    }

    public void traverse(Action<Tree<Category>> action) {
        for (CategoryTree categoryTree : this.categories.values()) {
                TreeUtil.traverse(categoryTree, action);
        }
    }

    public Map<CategoryClass, CategoryTree> getCategories() {
        return categories;
    }

    @Override
    public double getAmount() {
        return 0;
    }

    @Override
    public double getAmount(LocalDate localDate) {
        return 0;
    }

    @Override
    public double getAmount(LocalDate startDate, LocalDate endDate) {
        return 0;
    }
}

package de.raphaelmuesseler.financer.shared.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseCategory implements Serializable, AmountProvider {
    private static final long serialVersionUID = 6444376234610401363L;

    private final Map<CategoryClass, List<CategoryTree>> categories;

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

        public int getIndex() {
            return index;
        }

        public int getDatabaseIndex() {
            return index + 1;
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
    }


    public BaseCategory() {
        this.categories = new HashMap<>(4);

        for (CategoryClass categoryClass : CategoryClass.values()) {
            this.categories.put(categoryClass, new ArrayList<>());
        }
    }

    public List<CategoryTree> getCategoriesByCategoryClass(CategoryClass categoryClass) {
        return this.categories.get(categoryClass);
    }

    public Map<CategoryClass, List<CategoryTree>> getCategories() {
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
}

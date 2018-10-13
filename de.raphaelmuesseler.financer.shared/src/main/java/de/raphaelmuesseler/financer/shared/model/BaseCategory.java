package de.raphaelmuesseler.financer.shared.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseCategory implements Serializable {
    private static final long serialVersionUID = 6444376234610401363L;

    private final Map<CategoryName, Category> categories;

    public enum CategoryName {
        FIXED_REVENUE(0, "fixedRevenue"),
        VARIABLE_REVENUE(1, "variableRevenue"),
        FIXED_EXPENSES(2, "fixedExpenses"),
        VARIABLE_EXPENSES(3, "variableExpenses");

        private final int index;
        private final String name;

        CategoryName(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }


    public BaseCategory() {
        this.categories = new HashMap<>(4);
    }
}

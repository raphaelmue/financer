package org.financer.shared.domain.model.value.objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class CategoryClass implements Serializable {
    private static final long serialVersionUID = -8410423694511026919L;

    public enum Values {
        FIXED_REVENUE("fixedRevenue"),
        VARIABLE_REVENUE("variableRevenue"),
        FIXED_EXPENSES("fixedExpenses"),
        VARIABLE_EXPENSES("variableExpenses");

        private final String name;

        Values(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Values getCategoryClassByName(String name) {
            for (Values categoryClass : values()) {
                if (categoryClass.getName().equals(name)) {
                    return categoryClass;
                }
            }
            return null;
        }

        public static Values getCategoryClassByIndex(int index) {
            return values()[index];
        }

    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "category_class", nullable = false)
    private Values categoryClass;

    public CategoryClass() {
    }

    public CategoryClass(Values categoryClass) {
        this.categoryClass = categoryClass;
    }

    public boolean isFixed() {
        return (this.categoryClass == Values.FIXED_EXPENSES || this.categoryClass == Values.FIXED_REVENUE);
    }

    public boolean isRevenue() {
        return (this.categoryClass == Values.VARIABLE_REVENUE || this.categoryClass == Values.FIXED_REVENUE);
    }

    /*
     * Getters and Setters
     */

    public Values getCategoryClass() {
        return categoryClass;
    }
}

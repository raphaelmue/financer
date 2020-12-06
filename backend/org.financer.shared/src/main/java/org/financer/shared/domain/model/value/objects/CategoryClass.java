package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.financer.shared.exceptions.EnumNotFoundException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Embeddable
@Schema(description = "Value object for category class")
public class CategoryClass implements Serializable {
    private static final long serialVersionUID = -8410423694511026919L;

    @Schema(name = "CategoryClassEnum", description = "Values that can be applies to the category class")
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
            throw new EnumNotFoundException(Values.class, name);
        }

        public static Values getCategoryClassByIndex(int index) {
            return values()[index];
        }

    }

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "category_class", nullable = false)
    @Schema(description = "Category class", required = true, enumAsRef = true, example = "FIXED_REVENUE")
    private Values categoryClass;

    public CategoryClass() {
    }

    public CategoryClass(String categoryClass) {
        this.categoryClass = Values.getCategoryClassByName(categoryClass);
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

    public static List<CategoryClass> getAll() {
        return List.of(new CategoryClass(Values.FIXED_REVENUE),
                new CategoryClass(Values.FIXED_EXPENSES),
                new CategoryClass(Values.VARIABLE_REVENUE),
                new CategoryClass(Values.VARIABLE_EXPENSES));
    }
}

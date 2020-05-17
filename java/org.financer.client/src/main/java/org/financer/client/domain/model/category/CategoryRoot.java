package org.financer.client.domain.model.category;

import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.financer.util.collections.Tree;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoryRoot implements Serializable, AmountProvider, Tree {
    private static final long serialVersionUID = -2680853209855531689L;

    private final Set<Category> categories;

    public CategoryRoot(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Category> getCategoriesByClass(CategoryClass categoryClass) {
        return this.getCategoriesByClass(categoryClass.getCategoryClass());
    }

    public Set<Category> getCategoriesByClass(CategoryClass.Values categoryClass) {
        return this.categories.stream()
                .filter(category -> category.getCategoryClass().getCategoryClass().equals(categoryClass))
                .collect(Collectors.toSet());
    }

    public Set<Category> getCategories() {
        return categories;
    }

    @Override
    public Amount getAmount() {
        Amount amount = new Amount();
        for (Category category : categories) {
            amount = amount.add(category.getAmount());
        }
        return amount;
    }

    public Amount getAmount(CategoryClass.Values categoryClass) {
        Amount amount = new Amount();
        for (Category category : getCategoriesByClass(categoryClass)) {
            amount = amount.add(category.getAmount());
        }
        return amount;
    }

    @Override
    public Amount getAmount(ValueDate valueDate) {
        Amount amount = new Amount();
        for (Category category : categories) {
            amount = amount.add(category.getAmount(valueDate));
        }
        return amount;
    }

    public Amount getAmount(CategoryClass.Values categoryClass, ValueDate valueDate) {
        Amount amount = new Amount();
        for (Category category : getCategoriesByClass(categoryClass)) {
            amount = amount.add(category.getAmount(valueDate));
        }
        return amount;
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        Amount amount = new Amount();
        for (Category category : categories) {
            amount = amount.add(category.getAmount(timeRange));
        }
        return amount;
    }

    @Override
    public boolean isFixed() {
        throw new UnsupportedOperationException("The category root cannot have any category class");
    }

    @Override
    public boolean isRevenue() {
        throw new UnsupportedOperationException("The category root cannot have any category class");
    }

    @Override
    public void adjustAmountSign() {
        for (Category category : categories) {
            category.adjustAmountSign();
        }
    }

    @Override
    public Tree getParent() {
        return null;
    }

    @Override
    public Tree setParent(Tree parent) {
        throw new IllegalStateException("Root category cannot have a parent element.");
    }

    @Override
    public Set<? extends Tree> getChildren() {
        return this.categories;
    }
}

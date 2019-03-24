package de.raphaelmuesseler.financer.shared.model;

import de.raphaelmuesseler.financer.shared.model.transactions.AbstractTransaction;
import de.raphaelmuesseler.financer.util.collections.Tree;

import java.util.Set;

public interface CategoryTree extends Tree<Category>, AmountProvider {
    BaseCategory.CategoryClass getCategoryClass();

    void setCategoryClass(BaseCategory.CategoryClass categoryClass);

    Set<AbstractTransaction> getTransactions();
}

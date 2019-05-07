package de.raphaelmuesseler.financer.shared.model.categories;

import de.raphaelmuesseler.financer.shared.model.transactions.AmountProvider;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.Tree;

import java.util.Set;

public interface CategoryTree extends Tree<Category>, AmountProvider {
    Set<Transaction> getTransactions();

    @Override
    default boolean isRoot() {
        return getParent() == null || getParent().getValue().getName().equals("root");
    }
}

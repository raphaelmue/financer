package org.financer.shared.model.categories;

import org.financer.shared.model.transactions.AmountProvider;
import org.financer.shared.model.transactions.Transaction;
import org.financer.util.collections.Tree;

import java.io.Serializable;
import java.util.Set;

public interface CategoryTree extends Tree<Category>, AmountProvider, Serializable {
    Set<Transaction> getTransactions();

    @Override
    default boolean isRoot() {
        return getParent() == null || getParent().getValue().getName().equals("root");
    }
}

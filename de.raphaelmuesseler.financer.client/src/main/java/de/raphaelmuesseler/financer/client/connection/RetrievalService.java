package de.raphaelmuesseler.financer.client.connection;

import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.util.collections.Action;

import java.util.List;

public interface RetrievalService {
    void fetchAllData(User user, Action<Void> action);

    void fetchCategories(User user, AsyncCall<BaseCategory> asyncConnectionCall);
    void fetchTransactions(User user, AsyncCall<BaseCategory> asyncConnectionCall);
    void fetchFixedTransactions(User user, AsyncCall<BaseCategory> asyncConnectionCall);
}

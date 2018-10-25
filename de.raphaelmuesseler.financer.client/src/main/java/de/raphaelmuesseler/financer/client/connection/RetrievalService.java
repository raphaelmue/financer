package de.raphaelmuesseler.financer.client.connection;

import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.Action;

import java.util.List;

public interface RetrievalService {
    void fetchAllData(User user, Action action);

    void fetchCategories(User user, AsyncCall<BaseCategory> asyncConnectionCall);
    void fetchTransactions(User user, AsyncCall<List<Transaction>> asyncConnectionCall);
    void fetchFixedTransactions(User user, AsyncCall<List<FixedTransaction>> asyncConnectionCall);
}

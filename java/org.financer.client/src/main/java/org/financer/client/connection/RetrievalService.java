package org.financer.client.connection;

import org.financer.shared.connection.AsyncCall;
import org.financer.util.collections.Action;

public interface RetrievalService {
    void fetchAllData(User user, Action<Void> action);

    void fetchCategories(User user, AsyncCall<BaseCategory> asyncConnectionCall);

    void fetchTransactions(User user, AsyncCall<BaseCategory> asyncConnectionCall);

    void fetchFixedTransactions(User user, AsyncCall<BaseCategory> asyncConnectionCall);
}

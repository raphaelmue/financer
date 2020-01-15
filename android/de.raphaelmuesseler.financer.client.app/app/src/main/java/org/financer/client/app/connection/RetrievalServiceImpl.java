package org.financer.client.app.connection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.financer.client.app.local.LocalStorageImpl;
import org.financer.client.connection.AsyncConnectionCall;
import org.financer.client.connection.RetrievalService;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.local.LocalStorage;
import org.financer.shared.connection.AsyncCall;
import org.financer.shared.connection.ConnectionResult;
import org.financer.shared.model.categories.BaseCategory;
import org.financer.shared.model.user.User;
import org.financer.util.collections.Action;
import org.financer.util.collections.TreeUtil;
import org.financer.util.concurrency.FinancerExecutor;

public class RetrievalServiceImpl implements RetrievalService {
    private static RetrievalService INSTANCE = null;
    private LocalStorage localStorage = LocalStorageImpl.getInstance();

    public static RetrievalService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RetrievalServiceImpl();
        }
        return INSTANCE;
    }

    private RetrievalServiceImpl() {
    }

    @Override
    public void fetchAllData(User user, Action<Void> action) {
        this.fetchCategories(user, result -> fetchTransactions(user, transactions -> fetchFixedTransactions(user, fixedTransactions -> action.action(null))));
    }

    @Override
    public void fetchCategories(User user, AsyncCall<BaseCategory> asyncCall) {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("userId", user.getId());

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getUsersCategories", parameters, new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                BaseCategory categories = (BaseCategory) result.getResult();
                TreeUtil.numberItemsByValue(categories, (categoryTree, prefix) -> categoryTree.getValue().setPrefix(prefix));
                localStorage.writeObject("categories", categories);

                asyncCall.onSuccess(categories);
            }

            @Override
            public void onAfter() {
                asyncCall.onAfter();
            }
        }));
    }

    @Override
    public void fetchTransactions(User user, AsyncCall<BaseCategory> asyncCall) {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", localStorage.readObject("categories"));

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getTransactions", parameters, new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                BaseCategory categories = (BaseCategory) result.getResult();
                localStorage.writeObject("categories", categories);

                asyncCall.onSuccess(categories);
            }

            @Override
            public void onAfter() {
                asyncCall.onAfter();
            }
        }));
    }

    @Override
    public void fetchFixedTransactions(User user, AsyncCall<BaseCategory> asyncCall) {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", localStorage.readObject("categories"));

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getFixedTransactions", parameters, new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                BaseCategory categories = (BaseCategory) result.getResult();
                localStorage.writeObject("categories", categories);

                asyncCall.onSuccess(categories);
            }

            @Override
            public void onAfter() {
                asyncCall.onAfter();
            }
        }));
    }
}

package de.raphaelmuesseler.financer.client.app.connection;

import java.io.Serializable;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.connection.RetrievalService;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.Action;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

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

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getUsersCategories", parameters, new AndroidAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                BaseCategory categories = (BaseCategory) result.getResult();
                TreeUtil.numberItemsByValue(categories, (categoryTree, prefix) -> categoryTree.getValue().setPrefix(prefix));
                localStorage.writeObject("categories", categories);

                asyncCall.onSuccess(categories);
            }

            @Override
            public void onFailure(Exception exception) {
                asyncCall.onFailure(exception);
                if (!(exception instanceof ConnectException)) {
                    AndroidAsyncConnectionCall.super.onFailure(exception);
                }
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

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getTransactions", parameters, new AndroidAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                BaseCategory categories = (BaseCategory) result.getResult();
                localStorage.writeObject("categories", categories);

                asyncCall.onSuccess(categories);
            }

            @Override
            public void onFailure(Exception exception) {
                asyncCall.onFailure(exception);
                if (!(exception instanceof ConnectException)) {
                    AndroidAsyncConnectionCall.super.onFailure(exception);
                }
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

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getFixedTransactions", parameters, new AndroidAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                BaseCategory categories = (BaseCategory) result.getResult();
                localStorage.writeObject("categories", categories);

                asyncCall.onSuccess(categories);
            }

            @Override
            public void onFailure(Exception exception) {
                asyncCall.onFailure(exception);
                if (!(exception instanceof ConnectException)) {
                    AndroidAsyncConnectionCall.super.onFailure(exception);
                }
            }

            @Override
            public void onAfter() {
                asyncCall.onAfter();
            }
        }));
    }
}

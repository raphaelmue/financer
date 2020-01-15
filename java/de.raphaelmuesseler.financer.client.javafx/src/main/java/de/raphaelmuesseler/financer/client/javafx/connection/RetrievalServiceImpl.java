package de.raphaelmuesseler.financer.client.javafx.connection;

import de.raphaelmuesseler.financer.client.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.client.connection.RetrievalService;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.Action;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RetrievalServiceImpl implements RetrievalService {

    private static RetrievalService instance = null;
    private final LocalStorage localStorage = LocalStorageImpl.getInstance();


    public static RetrievalService getInstance() {
        if (instance == null) {
            instance = new RetrievalServiceImpl();
        }
        return instance;
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

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getUsersCategories", parameters,
                this.getAsyncCall(asyncCall)));
    }

    @Override
    public void fetchTransactions(User user, final AsyncCall<BaseCategory> asyncCall) {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", localStorage.readObject("categories"));

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getTransactions", parameters,
                this.getAsyncCall(asyncCall)));
    }

    @Override
    public void fetchFixedTransactions(User user, final AsyncCall<BaseCategory> asyncConnectionCall) {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", localStorage.readObject("categories"));


        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getFixedTransactions", parameters,
                this.getAsyncCall(asyncConnectionCall)));
    }

    private AsyncConnectionCall getAsyncCall(final AsyncCall<BaseCategory> asyncCall) {
        return new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                saveBaseCategory((BaseCategory) result.getResult());
                asyncCall.onSuccess((BaseCategory) result.getResult());
            }

            @Override
            public void onFailure(Exception exception) {
                asyncCall.onFailure(exception);
            }

            @Override
            public void onAfter() {
                asyncCall.onAfter();
            }
        };
    }

    private void saveBaseCategory(BaseCategory baseCategory) {
        TreeUtil.numberItemsByValue(baseCategory, (categoryTree, prefix) -> categoryTree.getValue().setPrefix(prefix));
        localStorage.writeObject("categories", baseCategory);
    }
}

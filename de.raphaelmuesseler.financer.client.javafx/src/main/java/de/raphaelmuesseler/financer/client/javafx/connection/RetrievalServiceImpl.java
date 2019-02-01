package de.raphaelmuesseler.financer.client.javafx.connection;

import de.raphaelmuesseler.financer.client.connection.RetrievalService;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.Action;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

import java.net.ConnectException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetrievalServiceImpl implements RetrievalService {

    private static RetrievalService INSTANCE = null;
    private final LocalStorage localStorage = LocalStorageImpl.getInstance();


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
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getUsersCategories", parameters, new JavaFXAsyncConnectionCall() {
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
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }
            }

            @Override
            public void onAfter() {
                asyncCall.onAfter();
            }
        }));
    }

    @Override
    public void fetchTransactions(User user, final AsyncCall<List<Transaction>> asyncCall) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getTransactions", parameters, new JavaFXAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                List<Transaction> transactions = (List<Transaction>) result.getResult();
                BaseCategory categories = (BaseCategory) localStorage.readObject("categories");
                for (Transaction transaction : transactions) {
                    CategoryTree categoryTree = (CategoryTree) TreeUtil.getByValue(categories,
                            transaction.getCategoryTree(), Comparator.comparingInt(Category::getId));
                    if (categoryTree != null) {
                        transaction.setCategoryTree(categoryTree);
                        categoryTree.getTransactions().add(transaction);
                    }
                }
                localStorage.writeObject("transactions", result.getResult());
                localStorage.writeObject("categories", categories);

                asyncCall.onSuccess(transactions);
            }

            @Override
            public void onFailure(Exception exception) {
                asyncCall.onFailure(exception);
                if (!(exception instanceof ConnectException)) {
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }
            }

            @Override
            public void onAfter() {
                asyncCall.onAfter();
            }
        }));
    }

    @Override
    public void fetchFixedTransactions(User user, final AsyncCall<List<FixedTransaction>> asyncConnectionCall) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "getFixedTransactions", parameters, new JavaFXAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                List<FixedTransaction> fixedTransactions = (List<FixedTransaction>) result.getResult();
                BaseCategory categories = (BaseCategory) localStorage.readObject("categories");
                for (FixedTransaction fixedTransaction : fixedTransactions) {
                    CategoryTree categoryTree = (CategoryTree) TreeUtil.getByValue(categories, fixedTransaction.getCategoryTree(),
                            Comparator.comparingInt(Category::getId));
                    if (categoryTree != null) {
                        fixedTransaction.setCategoryTree(categoryTree);
                        categoryTree.getTransactions().remove(fixedTransaction);
                        categoryTree.getTransactions().add(fixedTransaction);
                    }
                }
                localStorage.writeObject("fixedTransactions", result.getResult());
                localStorage.writeObject("categories", categories);

                asyncConnectionCall.onSuccess(fixedTransactions);
            }

            @Override
            public void onFailure(Exception exception) {
                asyncConnectionCall.onFailure(exception);
                if (!(exception instanceof ConnectException)) {
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }
            }

            @Override
            public void onAfter() {
                asyncConnectionCall.onAfter();
            }
        }));
    }
}

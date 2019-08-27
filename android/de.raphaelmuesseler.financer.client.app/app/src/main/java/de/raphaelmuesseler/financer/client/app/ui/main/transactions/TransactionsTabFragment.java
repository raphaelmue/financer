package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.connection.AndroidAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.app.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.app.ui.main.FinancerActivity;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

import static android.app.Activity.RESULT_OK;

public class TransactionsTabFragment extends Fragment {

    private static final int ADD_TRANSACTION_REQUEST = 1;  // The request code
    private static final int EDIT_TRANSACTION_REQUEST = 2;

    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), getContext());
    private List<VariableTransaction> transactions = new ArrayList<>();

    private ListView transactionListView;
    private SwipeRefreshLayout swipeRefreshLayoutTransactions;

    private boolean runningRefreshTask = false;

    public TransactionsTabFragment() {
        // Required empty public constructor
    }

    public static TransactionsTabFragment newInstance(BaseCategory categories) {
        TransactionsTabFragment fragment = new TransactionsTabFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("categories", categories);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transactions_tab, container, false);

        List<VariableTransaction> storedTransactions = LocalStorageImpl.getInstance().readList("transactions");
        if (storedTransactions != null) {
            transactions.addAll(storedTransactions);
        }

        this.swipeRefreshLayoutTransactions = rootView.findViewById(R.id.swipe_layout_transactions);
        this.swipeRefreshLayoutTransactions.setOnRefreshListener(this::refreshTransactions);

        this.transactionListView = rootView.findViewById(R.id.lv_transactions);
        this.transactionListView.setAdapter(new TransactionListViewAdapter(getContext(), transactions));
        this.transactionListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), TransactionActivity.class);
            intent.putExtra("transaction", (VariableTransaction) this.transactionListView.getItemAtPosition(position));
            startActivityForResult(intent, EDIT_TRANSACTION_REQUEST);
        });

        FloatingActionButton addTransactionBtn = rootView.findViewById(R.id.fab_transaction_tab_add_transaction);
        addTransactionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), TransactionActivity.class);
            startActivityForResult(intent, ADD_TRANSACTION_REQUEST);
        });

        new Thread(this::refreshTransactionsList).start();

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshTransactions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        User user = (User) LocalStorageImpl.getInstance().readObject("user");
        switch (requestCode) {
            case ADD_TRANSACTION_REQUEST:
                if (resultCode == RESULT_OK) {
                    VariableTransaction transaction = (VariableTransaction) data.getSerializableExtra("variableTransaction");

                    Map<String, Serializable> parameters = new HashMap<>();
                    parameters.put("variableTransaction", transaction);

                    FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user,
                            "addTransaction", parameters,
                            (AndroidAsyncConnectionCall) connectionResult -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                transaction.setId(((VariableTransaction) connectionResult.getResult()).getId());
                                BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
                                CategoryTree categoryTree = (CategoryTree) TreeUtil.getByValue(baseCategory, transaction.getCategoryTree(), (o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
                                categoryTree.getTransactions().add(transaction);
                                transaction.setCategoryTree(categoryTree);

                                LocalStorageImpl.getInstance().writeObject("categories", baseCategory);

                                refreshTransactionsList();
                                FinancerActivity.getFinancerApplication().showToast(Application.MessageType.SUCCESS, getString(R.string.success_added_transaction));
                            })));
                }
                break;
            case EDIT_TRANSACTION_REQUEST:
                if (resultCode == RESULT_OK) {
                    VariableTransaction transaction = (VariableTransaction) data.getSerializableExtra("variableTransaction");
                    transaction.getCategoryTree().getValue().setUser(user);

                    Map<String, Serializable> parameters = new HashMap<>();
                    parameters.put("variableTransaction", transaction);

                    FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "updateTransaction", parameters,
                            (AndroidAsyncConnectionCall) connectionResult -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
                                CategoryTree categoryTree = (CategoryTree) TreeUtil.getByValue(baseCategory, transaction.getCategoryTree(), (o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
                                categoryTree.getTransactions().remove(transaction);
                                categoryTree.getTransactions().add(transaction);
                                transaction.setCategoryTree(categoryTree);
                                LocalStorageImpl.getInstance().writeObject("categories", baseCategory);

                                refreshTransactionsList();
                                FinancerActivity.getFinancerApplication().showToast(Application.MessageType.SUCCESS,
                                        getString(R.string.success_updated_transaction));
                            })));
                }
                break;
        }
    }

    private void refreshTransactionsList() {
        transactions.clear();
        BaseCategory baseCategory = ((BaseCategory) LocalStorageImpl.getInstance().readObject("categories"));
        TreeUtil.traverse(baseCategory.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES), categoryTree -> {
            for (Transaction transaction : ((CategoryTree) categoryTree).getTransactions()) {
                transactions.add((VariableTransaction) transaction);
            }
        });
        TreeUtil.traverse(baseCategory.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE), categoryTree -> {
            for (Transaction transaction : ((CategoryTree) categoryTree).getTransactions()) {
                transactions.add((VariableTransaction) transaction);
            }
        });
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            transactions.sort((o1, o2) -> o2.getValueDate().compareTo(o1.getValueDate()));
            if (transactionListView != null) {
                ((TransactionListViewAdapter) transactionListView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    private void refreshTransactions() {
        if (!this.runningRefreshTask) {
            this.runningRefreshTask = true;
            RetrievalServiceImpl.getInstance().fetchTransactions((User) LocalStorageImpl.getInstance().readObject("user"), new AsyncCall<BaseCategory>() {
                @Override
                public void onSuccess(BaseCategory result) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(()
                            -> LocalStorageImpl.getInstance().writeObject("categories", result));
                }

                @Override
                public void onAfter() {
                    runningRefreshTask = false;
                    refreshTransactionsList();
                    Objects.requireNonNull(getActivity()).runOnUiThread(()
                            -> swipeRefreshLayoutTransactions.setRefreshing(false));
                }
            });
        }
    }

    private class TransactionListViewAdapter extends ArrayAdapter<VariableTransaction> {

        TransactionListViewAdapter(Context context, List<VariableTransaction> transactions) {
            super(context, R.layout.list_item_transaction, transactions);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            VariableTransaction transaction = getItem(position);

            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_transaction, parent, false);
            }

            if (transaction != null) {
                TextView categoryTextView = listItem.findViewById(R.id.tv_list_item_transaction_category);
                categoryTextView.setText(transaction.getCategoryTree().getValue().getName());

                TextView productTextView = listItem.findViewById(R.id.tv_list_item_transaction_product);
                productTextView.setText(!transaction.getProduct().isEmpty() ? transaction.getProduct() : transaction.getPurpose());

                TextView valueDateTextView = listItem.findViewById(R.id.tv_list_item_transaction_value_date);
                valueDateTextView.setText(formatter.formatDate(transaction.getValueDate()));

                TextView amountTextView = listItem.findViewById(R.id.tv_list_item_transaction_amount);
                amountTextView.setTextColor(transaction.getAmount() < 0 ?
                        ContextCompat.getColor(this.getContext(), R.color.error) :
                        ContextCompat.getColor(this.getContext(), R.color.success));
                amountTextView.setText(formatter.formatCurrency(transaction.getAmount()));
            }

            return listItem;
        }
    }
}

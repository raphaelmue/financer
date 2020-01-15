package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.app.ui.main.FinancerActivity;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
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

        this.swipeRefreshLayoutTransactions = rootView.findViewById(R.id.swipe_layout_transactions);
        this.swipeRefreshLayoutTransactions.setOnRefreshListener(this::refreshTransactions);

        this.transactionListView = rootView.findViewById(R.id.lv_transactions);
        this.transactionListView.setAdapter(new TransactionListViewAdapter(getContext()));
        this.transactionListView.setEmptyView(rootView.findViewById(R.id.tv_no_transactions));
        this.transactionListView.setOnItemClickListener((parent, view, position, id) -> {
            TransactionDetailFragment bottomDetailDialog = TransactionDetailFragment.newInstance(
                    (VariableTransaction) this.transactionListView.getItemAtPosition(position));
            bottomDetailDialog.show(getFragmentManager(), "test");
            bottomDetailDialog.setOnCancelListener(aVoid -> Objects.requireNonNull(getActivity()).runOnUiThread(this::refreshTransactionsList));
        });

        FloatingActionButton addTransactionBtn = rootView.findViewById(R.id.fab_transaction_tab_add_transaction);
        addTransactionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), TransactionActivity.class);
            startActivityForResult(intent, ADD_TRANSACTION_REQUEST);
        });

        new Thread(() -> Objects.requireNonNull(getActivity()).runOnUiThread(this::refreshTransactionsList)).start();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MaterialSearchView searchView = getActivity().findViewById(R.id.search_view);
        searchView.setMenuItem(item);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((TransactionListViewAdapter) transactionListView.getAdapter()).getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((TransactionListViewAdapter) transactionListView.getAdapter()).getFilter().filter(newText);
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
                getActivity().runOnUiThread(() -> refreshTransactionsList());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        if (requestCode == ADD_TRANSACTION_REQUEST) {
            if (resultCode == RESULT_OK) {
                VariableTransaction transaction = (VariableTransaction) data.getSerializableExtra("variableTransaction");

                Map<String, Serializable> parameters = new HashMap<>();
                parameters.put("variableTransaction", transaction);

                FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user,
                        "addTransaction", parameters, connectionResult ->
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
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
        }
    }

    private void refreshTransactionsList() {
        final List<VariableTransaction> transactions = new ArrayList<>();
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
        transactions.sort((o1, o2) -> o2.getValueDate().compareTo(o1.getValueDate()));
        if (transactionListView != null) {
            ((TransactionListViewAdapter) transactionListView.getAdapter()).setData(transactions);
            ((TransactionListViewAdapter) transactionListView.getAdapter()).notifyDataSetChanged();
        }
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
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        refreshTransactionsList();
                        swipeRefreshLayoutTransactions.setRefreshing(false);
                    });
                }
            });
        }
    }
}

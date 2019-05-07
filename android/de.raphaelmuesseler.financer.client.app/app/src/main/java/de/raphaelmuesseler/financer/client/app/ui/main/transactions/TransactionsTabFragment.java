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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;

public class TransactionsTabFragment extends Fragment {

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

        FloatingActionButton addTransactionBtn = rootView.findViewById(R.id.fab_transaction_tab_add_transaction);
        addTransactionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AddTransactionActivity.class);
            startActivity(intent);
        });

        this.refreshTransactions();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!this.runningRefreshTask) {
            transactions.clear();
            transactions.addAll(LocalStorageImpl.getInstance().readList("transactions"));
            transactions.sort((o1, o2) -> o2.getValueDate().compareTo(o1.getValueDate()));
            ((TransactionListViewAdapter) transactionListView.getAdapter()).notifyDataSetChanged();
        }
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

    private void refreshTransactions() {
        this.runningRefreshTask = true;
        RetrievalServiceImpl.getInstance().fetchTransactions((User) LocalStorageImpl.getInstance().readObject("user"), new AsyncCall<BaseCategory>() {
            @Override
            public void onSuccess(BaseCategory result) {
                transactions.clear();
                TreeUtil.traverse(result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES), categoryTree -> {
                    for (Transaction transaction : ((CategoryTree) categoryTree).getTransactions()) {
                        transactions.add((VariableTransaction) transaction);
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                exception.printStackTrace();
                transactions.clear();
                List<VariableTransaction> storedTransactions = LocalStorageImpl.getInstance().readList("transactions");
                if (storedTransactions != null) {
                    transactions.addAll(storedTransactions);
                }
            }

            @Override
            public void onAfter() {
                runningRefreshTask = false;
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    transactions.sort((o1, o2) -> o2.getValueDate().compareTo(o1.getValueDate()));
                    ((TransactionListViewAdapter) transactionListView.getAdapter()).notifyDataSetChanged();
                    swipeRefreshLayoutTransactions.setRefreshing(false);
                });
            }
        });
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

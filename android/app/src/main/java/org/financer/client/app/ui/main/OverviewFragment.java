package org.financer.client.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.financer.client.app.R;
import org.financer.client.app.local.LocalStorageImpl;
import org.financer.client.app.ui.main.transactions.TransactionActivity;
import org.financer.client.app.ui.main.transactions.TransactionDetailFragment;
import org.financer.client.app.ui.main.transactions.TransactionListViewAdapter;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.local.Application;
import org.financer.shared.model.categories.BaseCategory;
import org.financer.shared.model.categories.CategoryTree;
import org.financer.shared.model.transactions.Transaction;
import org.financer.shared.model.transactions.VariableTransaction;
import org.financer.shared.model.user.User;
import org.financer.util.collections.TreeUtil;
import org.financer.util.concurrency.FinancerExecutor;
import org.financer.util.date.DateUtil;

import static android.app.Activity.RESULT_OK;

public class OverviewFragment extends Fragment {

    private static final int REQUEST_ADD_TRANSACTION = 2;  // The request code

    private TextView expensesTextView;
    private SwipeRefreshLayout swipeRefreshLayoutOverview;
    private TextView balanceTextView;
    private TextView numberOfTransactionsTextView;
    private ListView lastTransactionsListView;

    private BaseCategory categories;

    private double balance;
    private double expenses;
    private AtomicInteger numberOfTransactions;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        FloatingActionButton addTransactionBtn = rootView.findViewById(R.id.fab_overview_add_transaction);
        addTransactionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), TransactionActivity.class);
            startActivityForResult(intent, REQUEST_ADD_TRANSACTION);
        });

        this.swipeRefreshLayoutOverview = rootView.findViewById(R.id.swipe_layout_overview);
        this.swipeRefreshLayoutOverview.setOnRefreshListener(() -> new Thread(this::init).start());

        this.balanceTextView = rootView.findViewById(R.id.tv_balance);
        this.expensesTextView = rootView.findViewById(R.id.tv_expenses);
        this.numberOfTransactionsTextView = rootView.findViewById(R.id.tv_number_transactions);
        this.lastTransactionsListView = rootView.findViewById(R.id.lv_last_transactions);
        this.lastTransactionsListView.setAdapter(new TransactionListViewAdapter(getContext()));
        this.lastTransactionsListView.setOnItemClickListener((parent, view, position, id) -> {
            TransactionDetailFragment bottomDetailDialog = TransactionDetailFragment.newInstance(
                    (VariableTransaction) this.lastTransactionsListView.getItemAtPosition(position));
            bottomDetailDialog.show(getFragmentManager(), bottomDetailDialog.getTag());
            bottomDetailDialog.setOnCancelListener(aVoid -> Objects.requireNonNull(getActivity()).runOnUiThread(this::initLastTransactionsListView));
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        new Thread(this::init).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_TRANSACTION) {
            if (resultCode == RESULT_OK) {

                VariableTransaction transaction = (VariableTransaction) data.getSerializableExtra("variableTransaction");

                Map<String, Serializable> parameters = new HashMap<>();
                parameters.put("variableTransaction", transaction);

                FinancerExecutor.getExecutor().execute(new ServerRequestHandler((User) LocalStorageImpl.getInstance().readObject("user"),
                        "addTransaction", parameters, connectionResult -> Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    transaction.setId(((VariableTransaction) connectionResult.getResult()).getId());
                    BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
                    CategoryTree categoryTree = (CategoryTree) TreeUtil.getByValue(baseCategory, transaction.getCategoryTree(), (o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
                    categoryTree.getTransactions().add(transaction);
                    transaction.setCategoryTree(categoryTree);

                    LocalStorageImpl.getInstance().writeObject("categories", baseCategory);
                    FinancerActivity.getFinancerApplication().showToast(Application.MessageType.SUCCESS, getString(R.string.success_added_transaction));
                    init();
                })));
            }
        }
    }

    private void init() {
        categories = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
        balance = categories.getAmount(LocalDate.now());
        expenses = categories.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES).getAmount(LocalDate.now());

        numberOfTransactions = new AtomicInteger();
        TreeUtil.traverse(categories.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES),
                object -> numberOfTransactions.addAndGet((int) ((CategoryTree) object).getTransactions().stream()
                        .filter(transaction -> DateUtil.checkIfMonthsAreEqual(((VariableTransaction) transaction).getValueDate(), LocalDate.now()))
                        .count()));
        TreeUtil.traverse(categories.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE),
                object -> numberOfTransactions.addAndGet((int) ((CategoryTree) object).getTransactions().stream()
                        .filter(transaction -> DateUtil.checkIfMonthsAreEqual(((VariableTransaction) transaction).getValueDate(), LocalDate.now()))
                        .count()));

        initNumberCard();
        initLastTransactionsListView();
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> this.swipeRefreshLayoutOverview.setRefreshing(false));
    }

    private void initNumberCard() {
        final User user = (User) LocalStorageImpl.getInstance().readObject("user");

        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            this.balanceTextView.setText(String.format(user.getSettings().getLanguage(), "%.2f", balance));
            this.balanceTextView.setTextColor(balance < 0 ?
                    ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.error) :
                    ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.success));
            this.expensesTextView.setText(String.format(user.getSettings().getLanguage(), "%.2f", expenses));
            this.expensesTextView.setTextColor(expenses < 0 ?
                    ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.error) :
                    ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.success));
            this.numberOfTransactionsTextView.setText(String.format("%s", numberOfTransactions.get()));
        });
    }

    private void initLastTransactionsListView() {
        List<VariableTransaction> lastTransactions = new ArrayList<>();
        BaseCategory baseCategory = ((BaseCategory) LocalStorageImpl.getInstance().readObject("categories"));
        TreeUtil.traverse(baseCategory.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES), categoryTree -> {
            for (Transaction transaction : ((CategoryTree) categoryTree).getTransactions()) {
                lastTransactions.add((VariableTransaction) transaction);
            }
        });
        TreeUtil.traverse(baseCategory.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE), categoryTree -> {
            for (Transaction transaction : ((CategoryTree) categoryTree).getTransactions()) {
                lastTransactions.add((VariableTransaction) transaction);
            }
        });
        lastTransactions.sort((o1, o2) -> o2.getValueDate().compareTo(o1.getValueDate()));
        if (lastTransactions.size() > 4) {
            lastTransactions.subList(4, lastTransactions.size()).clear();
        }
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            ((TransactionListViewAdapter) this.lastTransactionsListView.getAdapter()).setData(lastTransactions);
            ((TransactionListViewAdapter) this.lastTransactionsListView.getAdapter()).notifyDataSetChanged();
            ((TransactionListViewAdapter) this.lastTransactionsListView.getAdapter())
                    .setListViewHeightBasedOnChildren(this.lastTransactionsListView);
        });
    }
}

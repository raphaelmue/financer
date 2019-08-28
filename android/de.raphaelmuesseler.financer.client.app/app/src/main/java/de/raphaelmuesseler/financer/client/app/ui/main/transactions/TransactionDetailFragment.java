package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.connection.AndroidAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.app.ui.main.FinancerActivity;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.Action;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

import static android.app.Activity.RESULT_OK;

public class TransactionDetailFragment extends BottomSheetDialogFragment {

    private static final int EDIT_TRANSACTION_REQUEST = 2;
    private Toolbar toolbar;
    TextView amountTextView;
    TextView productTextView;
    TextView valueDateTextView;
    TextView categoryTextView;
    TextView purposeTextView;
    TextView shopTextView;

    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), getContext());

    private Action<Void> cancelAction;


    public TransactionDetailFragment() {
        // requires empty constructor
    }

    public static TransactionDetailFragment newInstance(VariableTransaction variableTransaction) {

        Bundle bundle = new Bundle();

        TransactionDetailFragment fragment = new TransactionDetailFragment();
        bundle.putSerializable("transaction", variableTransaction);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_detail, container,
                false);

        toolbar = view.findViewById(R.id.toolbar_transaction_details);
        amountTextView = view.findViewById(R.id.tv_transaction_amount);
        productTextView = view.findViewById(R.id.tv_transaction_product);
        valueDateTextView = view.findViewById(R.id.tv_transaction_value_date);
        categoryTextView = view.findViewById(R.id.tv_transaction_category);
        purposeTextView = view.findViewById(R.id.tv_transaction_purpose);
        shopTextView = view.findViewById(R.id.tv_transaction_shop);
        ImageButton editTransactionBtn = view.findViewById(R.id.btn_edit_transaction);

        if (getArguments() != null && !getArguments().isEmpty() && getArguments().getSerializable("transaction") != null) {
            final VariableTransaction transaction = (VariableTransaction) getArguments().getSerializable("transaction");

            this.fillLabels(transaction);
            editTransactionBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), TransactionActivity.class);
                VariableTransaction _transaction = (VariableTransaction) getArguments().getSerializable("transaction");
                intent.putExtra("transaction", _transaction);
                startActivityForResult(intent, EDIT_TRANSACTION_REQUEST);
            });
        }

        return view;
    }

    private void fillLabels(VariableTransaction transaction) {
        toolbar.setTitle(transaction.getProduct());
        amountTextView.setText(formatter.formatCurrency(transaction.getAmount()));
        productTextView.setText(transaction.getProduct());
        valueDateTextView.setText(formatter.formatDate(transaction.getValueDate()));
        categoryTextView.setText(formatter.formatCategoryName(transaction.getCategoryTree()));
        purposeTextView.setText(transaction.getPurpose());
        shopTextView.setText(transaction.getShop());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        User user = (User) LocalStorageImpl.getInstance().readObject("user");
        if (requestCode == EDIT_TRANSACTION_REQUEST) {
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

                            FinancerActivity.getFinancerApplication().showToast(Application.MessageType.SUCCESS,
                                    getString(R.string.success_updated_transaction));
                            fillLabels(transaction);
                            getArguments().remove("transaction");
                            getArguments().putSerializable("transaction", transaction);
                        })));
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        this.cancelAction.action(null);
    }

    public void setOnCancelListener(Action<Void> action) {
        this.cancelAction = action;
    }
}

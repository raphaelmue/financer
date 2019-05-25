package de.raphaelmuesseler.financer.client.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.connection.AndroidAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.app.ui.main.transactions.AddTransactionActivity;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

import static android.app.Activity.RESULT_OK;

public class OverviewFragment extends Fragment {

    private static final int ADD_TRANSACTION_REQUEST = 1;  // The request code

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
        View rootView =  inflater.inflate(R.layout.fragment_overview, container, false);

        FloatingActionButton addTransactionBtn = rootView.findViewById(R.id.fab_overview_add_transaction);
        addTransactionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AddTransactionActivity.class);
            startActivityForResult(intent, ADD_TRANSACTION_REQUEST);
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TRANSACTION_REQUEST) {
            if (resultCode == RESULT_OK) {

                VariableTransaction transaction = (VariableTransaction) data.getSerializableExtra("variableTransaction");

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("variableTransaction", transaction);

                FinancerExecutor.getExecutor().execute(new ServerRequestHandler((User) LocalStorageImpl.getInstance().readObject("user"),
                        "addTransaction", parameters, new AndroidAsyncConnectionCall() {
                    @Override
                    public void onSuccess(ConnectionResult connectionResult) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            transaction.setId(((VariableTransaction) connectionResult.getResult()).getId());
                            BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
                            CategoryTree categoryTree = (CategoryTree) TreeUtil.getByValue(baseCategory, transaction.getCategoryTree(), (o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
                            categoryTree.getTransactions().add(transaction);
                            transaction.setCategoryTree(categoryTree);

                            LocalStorageImpl.getInstance().writeObject("categories", baseCategory);
                        });
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        FinancerActivity.getFinancerApplication().showToast(Application.MessageType.ERROR, getString(R.string.something_went_wrong));
                    }
                }));
            }
        }
    }
}

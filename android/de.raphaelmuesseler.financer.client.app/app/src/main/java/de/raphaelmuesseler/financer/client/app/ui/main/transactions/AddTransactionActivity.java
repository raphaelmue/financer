package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.connection.AndroidAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText amountEditText, productEditText, purposeEditText, shopEditText, valueDateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        User user = (User) LocalStorageImpl.getInstance().readObject("user");
        BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");

        List<CategoryTree> categoryTreeList = new ArrayList<>();
        TreeUtil.traverse(baseCategory.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES),
                categoryTree -> categoryTreeList.add((CategoryTree) categoryTree));
        TreeUtil.traverse(baseCategory.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE),
                categoryTree -> categoryTreeList.add((CategoryTree) categoryTree));

        categoryTreeList.sort((o1, o2) -> o1.getValue().getName().compareTo(o2.getValue().getName()));

        Spinner categorySpinner = findViewById(R.id.sp_add_transaction_category);
        categorySpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categoryTreeList));

        this.amountEditText = findViewById(R.id.et_add_transaction_amount);
        this.productEditText = findViewById(R.id.et_add_transaction_product);
        this.purposeEditText = findViewById(R.id.et_add_transaction_purpose);
        this.shopEditText = findViewById(R.id.et_add_transaction_shop);
        this.valueDateEditText = findViewById(R.id.et_add_transaction_value_date);

        Button submitButton = findViewById(R.id.btn_add_transaction_submit);
        submitButton.setOnClickListener(view -> {
            Transaction transaction = new Transaction(-1,
                    Double.valueOf(amountEditText.getText().toString()),
                    (CategoryTree) categorySpinner.getSelectedItem(),
                    productEditText.getText().toString(),
                    purposeEditText.getText().toString(),
                    LocalDate.parse(valueDateEditText.getText().toString()),
                    shopEditText.getText().toString());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user", user);
            parameters.put("transaction", transaction);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "addTransaction", parameters, new AndroidAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult connectionResult) {
                    ((CategoryTree) categorySpinner.getSelectedItem()).getTransactions().add(transaction);
                    LocalStorageImpl.getInstance().writeObject("categories", baseCategory);

                    runOnUiThread(() -> finish());
                }

                @Override
                public void onFailure(Exception exception) {

                }
            }));
        });
    }
}

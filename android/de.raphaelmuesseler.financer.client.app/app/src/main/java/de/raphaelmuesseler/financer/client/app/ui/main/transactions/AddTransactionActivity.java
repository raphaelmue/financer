package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.connection.AndroidAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

public class AddTransactionActivity extends AppCompatActivity {

    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), this);


    private TextView valueDateEditText;
    private EditText amountEditText, productEditText, purposeEditText, shopEditText;

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

        categoryTreeList.sort((o1, o2) -> formatter.formatCategoryName(o1).compareTo(formatter.formatCategoryName(o2)));

        Spinner categorySpinner = findViewById(R.id.sp_add_transaction_category);
        ArrayAdapter<CategoryTree> adapter = new CategorySpinnerAdapter(this, categoryTreeList);
        categorySpinner.setAdapter(adapter);

        this.amountEditText = findViewById(R.id.et_add_transaction_amount);
        this.productEditText = findViewById(R.id.et_add_transaction_product);
        this.purposeEditText = findViewById(R.id.et_add_transaction_purpose);
        this.shopEditText = findViewById(R.id.et_add_transaction_shop);
        this.valueDateEditText = findViewById(R.id.tv_add_transaction_value_date);
        this.valueDateEditText.setText(formatter.formatDate(LocalDate.now()));

        this.valueDateEditText.setOnClickListener(v -> new DatePickerDialog(
                AddTransactionActivity.this,
                (view, year, monthOfYear, dayOfMonth) ->
                        valueDateEditText.setText(formatter.formatDate(LocalDate.of(year, monthOfYear + 1, dayOfMonth))),
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue() - 1,
                LocalDate.now().getDayOfMonth()).show());

        Button submitButton = findViewById(R.id.btn_add_transaction_submit);
        submitButton.setOnClickListener(view -> {
            boolean cancel = false;

            if (amountEditText.getText().toString().isEmpty()) {
                amountEditText.setError(getString(R.string.error_field_required));
                cancel = true;
            }
            if (valueDateEditText.getText().toString().isEmpty()) {
                valueDateEditText.setError(getString(R.string.error_field_required));
                cancel = true;
            }

            if (!cancel) {
                Transaction transaction = new Transaction(-1,
                        Double.valueOf(amountEditText.getText().toString().replace(",", ".")),
                        (CategoryTree) categorySpinner.getSelectedItem(),
                        productEditText.getText().toString(),
                        purposeEditText.getText().toString(),
                        LocalDate.parse(valueDateEditText.getText().toString(), DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                .withLocale(((LocalSettings) LocalStorageImpl.getInstance().readObject("localSettings")).getLanguage())),
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
            }
        });
    }

    private class CategorySpinnerAdapter extends ArrayAdapter<CategoryTree> {
        CategorySpinnerAdapter(Context context, List<CategoryTree> items) {
            super(context, R.layout.support_simple_spinner_dropdown_item, items);
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            CategoryTree categoryTree = getItem(position);

            final View view;
            final TextView text;

            if (convertView == null) {
                view = LayoutInflater.from(this.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
            } else {
                view = convertView;
            }

            text = (TextView) view;

            if (categoryTree != null) {
                text.setText(formatter.formatCategoryName(categoryTree));
            }

            return view;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return this.getView(position, convertView, parent);
        }
    }
}

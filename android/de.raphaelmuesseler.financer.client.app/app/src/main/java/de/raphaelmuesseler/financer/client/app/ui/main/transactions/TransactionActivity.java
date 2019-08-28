package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.app.ui.components.DatePicker;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;

public class TransactionActivity extends AppCompatActivity {

    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), this);

    private DatePicker valueDateEditText;
    private EditText amountEditText;
    private EditText productEditText;
    private EditText purposeEditText;
    private EditText shopEditText;
    private Spinner categorySpinner;

    private VariableTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        Toolbar toolbar = findViewById(R.id.transaction_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> runOnUiThread(this::finish));

        BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");

        List<CategoryTree> categoryTreeList = new ArrayList<>();
        TreeUtil.traverse(baseCategory.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES),
                categoryTree -> categoryTreeList.add((CategoryTree) categoryTree));
        TreeUtil.traverse(baseCategory.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE),
                categoryTree -> categoryTreeList.add((CategoryTree) categoryTree));

        categoryTreeList.sort((o1, o2) -> formatter.formatCategoryName(o1).compareTo(formatter.formatCategoryName(o2)));

        categorySpinner = findViewById(R.id.sp_add_transaction_category);
        ArrayAdapter<CategoryTree> adapter = new CategorySpinnerAdapter(this, categoryTreeList);
        categorySpinner.setAdapter(adapter);

        this.amountEditText = findViewById(R.id.et_add_transaction_amount);
        this.productEditText = findViewById(R.id.et_add_transaction_product);
        this.purposeEditText = findViewById(R.id.et_add_transaction_purpose);
        this.shopEditText = findViewById(R.id.et_add_transaction_shop);
        this.valueDateEditText = findViewById(R.id.tv_add_transaction_value_date);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            this.transaction = (VariableTransaction) bundle.get("transaction");
            if (transaction != null) {
                this.setTitle(R.string.transaction);

                User user = (User) LocalStorageImpl.getInstance().readObject("user");
                this.amountEditText.setText(String.format(user.getSettings().getLanguage(), "%.2f", transaction.getAmount()));
                this.productEditText.setText(transaction.getProduct());
                this.purposeEditText.setText(transaction.getPurpose());
                this.shopEditText.setText(transaction.getShop());
                this.valueDateEditText.setValue(transaction.getValueDate());
                this.categorySpinner.setSelection(adapter.getPosition(transaction.getCategoryTree()));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_transaction_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_check) {
            submitAddTransaction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void submitAddTransaction() {
        User user = (User) LocalStorageImpl.getInstance().readObject("user");

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
            int id = this.transaction != null ? this.transaction.getId() : 0;
            final VariableTransaction transaction = new VariableTransaction(id,
                    Double.valueOf(amountEditText.getText().toString().replace(",", ".")),
                    LocalDate.parse(valueDateEditText.getText().toString(), DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                            .withLocale(((User) LocalStorageImpl.getInstance().readObject("user")).getSettings().getLanguage())),
                    (CategoryTree) categorySpinner.getSelectedItem(),
                    productEditText.getText().toString(),
                    purposeEditText.getText().toString(),
                    shopEditText.getText().toString());

            if (user.getSettings().isChangeAmountSignAutomatically()) {
                transaction.adjustAmountSign();
            }

            ((CategoryTree) categorySpinner.getSelectedItem()).getTransactions().add(transaction);
            Intent data = new Intent();
            data.putExtra("variableTransaction", transaction);
            setResult(RESULT_OK, data);

            finish();
        }
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

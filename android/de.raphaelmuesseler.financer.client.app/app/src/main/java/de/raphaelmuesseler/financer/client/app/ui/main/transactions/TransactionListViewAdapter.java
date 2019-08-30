package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;

public class TransactionListViewAdapter extends ArrayAdapter<VariableTransaction> {

    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), getContext());

    public TransactionListViewAdapter(Context context, List<VariableTransaction> transactions) {
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        int totalHeight = 0;
        for (int i = 0; i < this.getCount(); i++) {
            View listItem = this.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (this.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
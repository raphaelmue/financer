package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.format.AndroidFormatter;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;

public class TransactionListViewAdapter extends ArrayAdapter<VariableTransaction> {

    private final Formatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), getContext());
    private final List<VariableTransaction> transactions;
    private final List<VariableTransaction> filteredData;
    private final Filter filter = new Filter() {
        private String oldQuery = "";

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = transactions;
                results.count = ((List) results.values).size();

                oldQuery = "";
            } else {
                final List<VariableTransaction> filteredTransaction = new ArrayList<>();

                for (VariableTransaction transaction : (oldQuery.length() == constraint.length() - 1 ? filteredData : transactions)) {
                    if (transaction.getProduct().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            transaction.getPurpose().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            transaction.getShop().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            formatter.formatCategoryName(transaction.getCategoryTree()).toUpperCase().contains(constraint.toString().toUpperCase()))
                        filteredTransaction.add(transaction);
                }

                results.values = filteredTransaction;
                results.count = filteredTransaction.size();

                oldQuery = constraint.toString();
            }


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                notifyDataSetInvalidated();
                filteredData.clear();
            } else {
                filteredData.clear();
                filteredData.addAll((List<VariableTransaction>) results.values);
                notifyDataSetChanged();
            }
        }
    };

    public TransactionListViewAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public TransactionListViewAdapter(Context context, List<VariableTransaction> transactions) {
        super(context, R.layout.list_item_transaction, transactions);
        this.transactions = new ArrayList<>(transactions);
        this.filteredData = transactions;
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

    public void setData(List<VariableTransaction> transactions) {
        this.transactions.clear();
        this.transactions.addAll(transactions);
        this.filteredData.clear();
        this.filteredData.addAll(transactions);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }
}
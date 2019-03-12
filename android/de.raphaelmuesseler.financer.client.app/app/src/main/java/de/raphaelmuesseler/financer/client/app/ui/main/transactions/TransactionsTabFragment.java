package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.FormatterImpl;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;

public class TransactionsTabFragment extends Fragment {

    private final Formatter formatter = new FormatterImpl(LocalStorageImpl.getInstance());

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transactions_tab, container, false);

        List<Transaction> transactions = LocalStorageImpl.getInstance().readList("transactions");

        ListView transactionListView = rootView.findViewById(R.id.lv_transactions);
        transactionListView.setAdapter(new TransactionListViewAdapter(getContext(), transactions));

        return rootView;
    }

    private class TransactionListViewAdapter extends ArrayAdapter<Transaction> {

        TransactionListViewAdapter(Context context, List<Transaction> transactions) {
            super(context, R.layout.list_item_transaction, transactions);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Transaction transaction = getItem(position);

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

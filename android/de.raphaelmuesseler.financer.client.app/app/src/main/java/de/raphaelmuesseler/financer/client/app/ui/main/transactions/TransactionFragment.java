package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;

public class TransactionFragment extends Fragment {

    public TransactionFragment() {
        // Required empty public constructor
    }

    public static TransactionFragment newInstance() {
        return new TransactionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        ViewPager viewPager = view.findViewById(R.id.transaction_view_pager);
        TabLayout tabLayout = view.findViewById(R.id.transactions_tabs);

        viewPager.setAdapter(new TransactionFragmentPagerAdapter(getChildFragmentManager(), getContext(),
                (BaseCategory) LocalStorageImpl.getInstance().readObject("categories")));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}

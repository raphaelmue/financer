package de.raphaelmuesseler.financer.client.app.ui.main.transactions;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.raphaelmuesseler.financer.client.app.R;

public class TransactionFragmentPagerAdapter extends FragmentPagerAdapter {
    private final Context context;

    public TransactionFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return TransactionOverviewTabFragment.newInstance();
            case 1:
                return TransactionsTabFragment.newInstance();
            case 2:
                return FixedTransactionsTabFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return this.context.getString(R.string.overview);
            case 1:
                return this.context.getString(R.string.transactions);
            case 2:
                return this.context.getString(R.string.fixed_transactions);
        }
        return null;
    }
}

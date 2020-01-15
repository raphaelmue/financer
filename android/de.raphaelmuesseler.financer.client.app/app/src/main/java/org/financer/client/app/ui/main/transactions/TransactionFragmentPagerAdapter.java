package org.financer.client.app.ui.main.transactions;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.financer.client.app.R;
import org.financer.shared.model.categories.BaseCategory;

public class TransactionFragmentPagerAdapter extends FragmentPagerAdapter {
    private final Context context;
    private final BaseCategory baseCategory;

    TransactionFragmentPagerAdapter(FragmentManager fm, Context context, BaseCategory baseCategory) {
        super(fm);
        this.context = context;
        this.baseCategory = baseCategory;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return TransactionsTabFragment.newInstance(this.baseCategory);
            case 1:
                return FixedTransactionsTabFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return this.context.getString(R.string.transactions);
            case 1:
                return this.context.getString(R.string.fixed_transactions);
        }
        return null;
    }
}

package de.raphaelmuesseler.financer.client.app.format;

import android.content.Context;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.format.FormatterImpl;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;

public class AndroidFormatter extends FormatterImpl {
    private final Context context;

    public AndroidFormatter(LocalStorage localStorage, Context context) {
        super(localStorage);
        this.context = context;
    }

    @Override
    public String formatCategoryName(CategoryTree categoryTree) {
        return this.formatCategoryName(categoryTree.getValue());
    }

    @Override
    public String formatCategoryName(Category category) {
        StringBuilder stringBuilder = new StringBuilder();
        if (category.getPrefix() != null) {
            stringBuilder.append(category.getPrefix());
        }

        stringBuilder.append(" ");

        if (category.getName().equals(BaseCategory.CategoryClass.FIXED_EXPENSES.getName())) {
            stringBuilder.append(this.context.getString(R.string.fixed_expenses));
        } else if (category.getName().equals(BaseCategory.CategoryClass.FIXED_REVENUE.getName())) {
            stringBuilder.append(this.context.getString(R.string.fixed_revenue));
        } else if (category.getName().equals(BaseCategory.CategoryClass.VARIABLE_EXPENSES.getName())) {
            stringBuilder.append(this.context.getString(R.string.variable_expenses));
        } else if (category.getName().equals(BaseCategory.CategoryClass.VARIABLE_REVENUE.getName())) {
            stringBuilder.append(this.context.getString(R.string.variable_revenue));
        } else {
            stringBuilder.append(category.getName());
        }

        return stringBuilder.toString();
    }
}

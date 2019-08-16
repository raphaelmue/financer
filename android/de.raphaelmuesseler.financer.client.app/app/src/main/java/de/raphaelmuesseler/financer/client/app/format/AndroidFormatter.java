package de.raphaelmuesseler.financer.client.app.format;

import android.content.Context;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.raphaelmuesseler.financer.client.app.R;
import de.raphaelmuesseler.financer.client.format.FormatterImpl;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.exceptions.FinancerException;
import de.raphaelmuesseler.financer.shared.exceptions.NotAuthorizedException;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;

public class AndroidFormatter extends FormatterImpl {
    private final Context context;

    public AndroidFormatter(LocalStorage localStorage, Context context) {
        super(localStorage);
        this.context = context;
    }

    @Override
    public String formatExceptionMessage(Exception exception) {
        String message = this.context.getString(R.string.err_something_went_wrong);

        try {
            throw exception;
        } catch (NotAuthorizedException var4) {
            message = this.context.getString(R.string.err_not_authorized);;
        } catch (UnknownHostException var5) {
            message = this.context.getString(R.string.err_database_unavailable);;
        } catch (ConnectException var6) {
            message = this.context.getString(R.string.err_server_unavailable);;
        } catch (FinancerException ignored) {
        } catch (Exception var8) {
            Logger.getLogger("FinancerApplication").log(Level.SEVERE, var8.getMessage(), var8);
        }

        return message;
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

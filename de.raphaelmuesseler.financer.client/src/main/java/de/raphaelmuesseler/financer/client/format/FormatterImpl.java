package de.raphaelmuesseler.financer.client.format;

import de.raphaelmuesseler.financer.client.local.LocalSettings;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.exceptions.FinancerException;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.user.User;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class FormatterImpl implements Formatter {
    private final LocalSettings localSettings;
    private final User user;


    public FormatterImpl(LocalStorage localStorage) {
        this.localSettings = (LocalSettings) localStorage.readObject("localSettings");
        this.user = (User) localStorage.readObject("user");
    }

    public FormatterImpl(LocalSettings localSettings, User user) {
        this.localSettings = localSettings;
        this.user = user;
    }

    @Override
    public String formatExceptionMessage(FinancerException exception) {
        return I18N.get(exception.getDisplayMessage());
    }

    @Override
    public String formatCurrency(Double amount) {
        StringBuilder result = new StringBuilder(String.format(localSettings.getLanguage(), "%.2f", amount));
        if (this.user.getSettings().getCurrency() != null) {
            result.append(" ");
            if (this.user.getSettings().isShowCurrencySign()) {
                result.append(this.user.getSettings().getCurrency().getSymbol());
            } else {
                result.append(this.user.getSettings().getCurrency().getCurrencyCode());
            }
        }
        return result.toString();
    }

    @Override
    public String formatCategoryName(Category category) {
        if (category.getPrefix() != null) {
            return category.getPrefix() + " " + category.getName();
        } else {
            return category.getName();
        }
    }

    @Override
    public String formatCategoryName(CategoryTree categoryTree) {
        if (categoryTree.getValue().getPrefix() != null) {
            return categoryTree.getValue().getPrefix() + " " +
                    (categoryTree.getValue().getName().equals(categoryTree.getCategoryClass().getName()) ?
                            I18N.get(categoryTree.getValue().getName()) : categoryTree.getValue().getName());
        } else {
            return (categoryTree.getValue().getName().equals(categoryTree.getCategoryClass().getName()) ?
                    I18N.get(categoryTree.getValue().getName()) : categoryTree.getValue().getName());
        }
    }

    @Override
    public String formatDate(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(localSettings.getLanguage());
        return localDate.format(formatter);
    }
}

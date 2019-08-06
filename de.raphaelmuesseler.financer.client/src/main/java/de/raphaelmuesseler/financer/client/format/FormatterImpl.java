package de.raphaelmuesseler.financer.client.format;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.exceptions.FinancerException;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public abstract class FormatterImpl implements Formatter {
    private final User user;


    public FormatterImpl(LocalStorage localStorage) {
        this.user = (User) localStorage.readObject("user");
    }

    public FormatterImpl(User user) {
        this.user = user;
    }

    @Override
    public String formatExceptionMessage(FinancerException exception) {
        return I18N.get(exception.getDisplayMessage());
    }

    @Override
    public String formatCurrency(Double amount) {
        StringBuilder result = new StringBuilder(String.format(user.getSettings().getLanguage(), "%.2f", amount));
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
    public String formatDate(LocalDate localDate) {
        Locale locale = user == null ? Locale.ENGLISH : user.getSettings().getLanguage();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        return localDate.format(formatter);
    }

    @Override
    public LocalDate convertStringToLocalDate(String dateString) {
        Locale locale = user == null ? Locale.ENGLISH : user.getSettings().getLanguage();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        return LocalDate.parse(dateString, formatter);
    }
}

package de.raphaelmuesseler.financer.client.format;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.exceptions.FinancerException;
import de.raphaelmuesseler.financer.shared.exceptions.NotAuthorizedException;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.user.User;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FormatterImpl implements Formatter {
    protected final User user;


    public FormatterImpl(LocalStorage localStorage) {
        this.user = (User) localStorage.readObject("user");
    }

    public FormatterImpl(User user) {
        this.user = user;
    }

    @Override
    public String formatExceptionMessage(Exception exception) {
        String key = "errSomethingWentWrong";

        try {
            throw exception;
        } catch (NotAuthorizedException e) {
            key = "errNotAuthorized";
        } catch (UnknownHostException e) {
            key = "errDatabaseUnavailable";
        } catch (ConnectException connectException) {
            key = "errServerUnavailable";
        } catch (FinancerException financerException) {
            key = financerException.getKey();
        } catch (Exception e) {
            Logger.getLogger("FinancerApplication").log(Level.SEVERE, e.getMessage(), e);
        }

        return I18N.get(key);
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

package org.financer.client.javafx.format;

import org.financer.client.format.Formatter;
import org.financer.client.format.I18N;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.model.categories.Category;
import org.financer.shared.model.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
@Tag("unit")
public class JavaFXFormatterTest {
    private static User user;
    private static Formatter formatter;

    @BeforeEach
    public void init() {
        user = new User();

        I18N.setLocalStorage(LocalStorageImpl.getInstance());

        user.getSettings().setLanguage(Locale.ENGLISH);
        user.getSettings().setShowCurrencySign(false);
        user.getSettings().setCurrency(Currency.getInstance("USD"));

        formatter = new JavaFXFormatter(user);
        LocalStorageImpl.getInstance().writeObject("user", user);
    }

    @Test
    public void testCurrencyFormat() {
        final double amount = 5.387;

        String currencyFormat = formatter.formatCurrency(amount);
        Assertions.assertEquals(String.format(Locale.ENGLISH, "%.2f", amount) + " " + Currency.getInstance("USD").getCurrencyCode(), currencyFormat);

        user.getSettings().setShowCurrencySign(true);

        currencyFormat = formatter.formatCurrency(amount);
        Assertions.assertEquals(String.format(Locale.ENGLISH, "%.2f", amount) + " " + Currency.getInstance("USD").getSymbol(), currencyFormat);

        user.getSettings().setLanguage(Locale.GERMAN);

        currencyFormat = formatter.formatCurrency(amount);
        Assertions.assertEquals(String.format(Locale.GERMAN, "%.2f", amount).replace(".", ",") + " " +
                Currency.getInstance("USD").getSymbol(), currencyFormat);
    }

    @Test
    public void testCategoryFormat() {
        final Category category = new Category("testCategory");
        category.setPrefix("testPrefix1");

        String categoryFormat = formatter.formatCategoryName(category);
        Assertions.assertEquals(category.getPrefix() + " " + category.getName(), categoryFormat);

        category.setName("root");
        categoryFormat = formatter.formatCategoryName(category);
        Assertions.assertEquals(category.getPrefix() + " " + "Balance", categoryFormat);

        final Category categoryClass = new Category("fixedExpenses");
        categoryClass.setPrefix("testPrefix2");

        categoryFormat = formatter.formatCategoryName(categoryClass);
        Assertions.assertEquals(categoryClass.getPrefix() + " Fixed Expenses", categoryFormat);

    }

    @Test
    public void testDateFormat() {
        final LocalDate localDate = LocalDate.of(2019, 3, 20);
        Assertions.assertEquals("Mar 20, 2019", formatter.formatDate(localDate));

        user.getSettings().setLanguage(Locale.GERMAN);

        Assertions.assertEquals("20.03.2019", formatter.formatDate(localDate));
    }
}

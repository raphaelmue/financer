package de.raphaelmuesseler.financer.client.javafx.format;

import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import de.raphaelmuesseler.financer.client.local.LocalSettingsImpl;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class FormatTest {
    private static LocalSettings settings;
    private static User user;
    private static Formatter formatter;

    @BeforeEach
    public void init() {
        settings = new LocalSettingsImpl();
        user = new User();

        I18N.setLocalStorage(LocalStorageImpl.getInstance());

        settings.setLanguage(Locale.ENGLISH);
        user.getSettings().setShowCurrencySign(false);
        user.getSettings().setCurrency(Currency.getInstance("USD"));

        formatter = new JavaFXFormatter(settings, user);
    }

    @Test
    public void testCurrencyFormat() {
        final double amount = 5.387;

        String currencyFormat = formatter.formatCurrency(amount);
        Assertions.assertEquals(String.format(Locale.ENGLISH, "%.2f", amount) + " " + Currency.getInstance("USD").getCurrencyCode(), currencyFormat);

        user.getSettings().setShowCurrencySign(true);

        currencyFormat = formatter.formatCurrency(amount);
        Assertions.assertEquals(String.format(Locale.ENGLISH, "%.2f", amount) + " " + Currency.getInstance("USD").getSymbol(), currencyFormat);

        settings.setLanguage(Locale.GERMAN);

        currencyFormat = formatter.formatCurrency(amount);
        Assertions.assertEquals(String.format(Locale.GERMAN, "%.2f", amount).replace(".", ",") + " " +
                Currency.getInstance("USD").getSymbol(), currencyFormat);
    }

    @Test
    public void testCategoryFormat() {
        final Category category = new Category(1, "testCategory", 1, 0);
        category.setPrefix("testPrefix1");

        String categoryFormat = formatter.formatCategoryName(category);
        Assertions.assertEquals(category.getPrefix() + " " + category.getName(), categoryFormat);

        final Category categoryClass = new Category(2, "fixedExpenses", 1, -1);
        categoryClass.setPrefix("testPrefix2");

        categoryFormat = formatter.formatCategoryName(categoryClass);
        Assertions.assertEquals(categoryClass.getPrefix() + " Fixed Expenses", categoryFormat);
    }

    @Test
    public void testDateFormat() {
        final LocalDate localDate = LocalDate.of(2019, 3, 20);
        Assertions.assertEquals("Mar 20, 2019", formatter.formatDate(localDate));

        settings.setLanguage(Locale.GERMAN);

        Assertions.assertEquals("20.03.2019", formatter.formatDate(localDate));
    }
}

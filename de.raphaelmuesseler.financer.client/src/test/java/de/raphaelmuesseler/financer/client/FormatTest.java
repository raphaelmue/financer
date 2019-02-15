package de.raphaelmuesseler.financer.client;

import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.FormatterImpl;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import de.raphaelmuesseler.financer.client.local.LocalSettingsImpl;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Locale;

public class FormatTest {
    private static LocalSettings settings;
    private static User user;
    private static Formatter formatter;

    @BeforeAll
    public static void init() {
        settings = new LocalSettingsImpl();
        user = new User();

        settings.setLanguage(Locale.ENGLISH);
        user.getSettings().setShowCurrencySign(false);
        user.getSettings().setCurrency(Currency.getInstance("USD"));

        formatter = new FormatterImpl(settings, user);
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
        category.setPrefix("testPrefix");

        String categoryFormate = formatter.formatCategoryName(category);
        Assertions.assertEquals(category.getPrefix() + " " + category.getName(), categoryFormate);
    }
}

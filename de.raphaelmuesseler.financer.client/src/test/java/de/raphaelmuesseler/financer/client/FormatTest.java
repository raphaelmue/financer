package de.raphaelmuesseler.financer.client;

import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.local.Settings;
import de.raphaelmuesseler.financer.shared.model.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Locale;

class FormatTest {
    private static Settings settings;

    @BeforeAll
    static void init() {
        settings = new Settings();
        settings.setCurrency(Currency.getInstance("USD"));
        settings.setLanguage(Locale.ENGLISH);
        settings.setShowCurrencySign(false);

        Formatter.setSettings(settings);
    }

    @Test
    void testCurrencyFormat() {
        final double amount = 5.387;

        String currencyFormat = Formatter.formatCurrency(amount);
        Assertions.assertEquals(Double.toString(amount) + " " + Currency.getInstance("USD").getCurrencyCode(), currencyFormat);

        settings.setShowCurrencySign(true);
        Formatter.setSettings(settings);

        currencyFormat = Formatter.formatCurrency(amount);
        Assertions.assertEquals(Double.toString(amount) + " " + Currency.getInstance("USD").getSymbol(), currencyFormat);

        settings.setLanguage(Locale.GERMAN);
        Formatter.setSettings(settings);

        currencyFormat = Formatter.formatCurrency(amount);
        Assertions.assertEquals(Double.toString(amount).replace(".", ",") + " " +
                Currency.getInstance("USD").getSymbol(), currencyFormat);
    }

    @Test
    void testCategoryFormat() {
        final Category category = new Category(1, "testCategory", 1, 0);
        category.setPrefix("testPrefix");

        String categoryFormate = Formatter.formatCategoryName(category);
        Assertions.assertEquals(category.getPrefix() + " " + category.getName(), categoryFormate);
    }
}

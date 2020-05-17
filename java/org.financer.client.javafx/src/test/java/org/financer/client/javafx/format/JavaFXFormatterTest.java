package org.financer.client.javafx.format;

import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.Formatter;
import org.financer.client.format.I18N;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.SettingPair;
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

        user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, Locale.ENGLISH);
        user.putOrUpdateSettingProperty(SettingPair.Property.CURRENCY, Currency.getInstance("USD"));
        user.putOrUpdateSettingProperty(SettingPair.Property.SHOW_CURRENCY_SIGN, false);

        formatter = new JavaFXFormatter(user);
        LocalStorageImpl.getInstance().writeObject("user", user);
    }

    @Test
    public void testCurrencyFormat() {
        final Amount amount = new Amount(5.387);

        String currencyFormat = formatter.format(amount);
        Assertions.assertEquals(String.format(Locale.ENGLISH, "%.2f", amount.getAmount()) + " " + Currency.getInstance("USD").getCurrencyCode(), currencyFormat);

        user.putOrUpdateSettingProperty(SettingPair.Property.SHOW_CURRENCY_SIGN, true);

        currencyFormat = formatter.format(amount);
        Assertions.assertEquals(String.format(Locale.ENGLISH, "%.2f", amount.getAmount()) + " " + Currency.getInstance("USD").getSymbol(), currencyFormat);

        user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, Locale.GERMAN);

        currencyFormat = formatter.format(amount);
        Assertions.assertEquals(String.format(Locale.GERMAN, "%.2f", amount.getAmount()).replace(".", ",") + " " +
                Currency.getInstance("USD").getSymbol(), currencyFormat);
    }

    @Test
    public void testCategoryFormat() {
        final Category category = new Category().setName("testCategory");

        String categoryFormat = formatter.format(category);
        Assertions.assertEquals(category.getPrefix() + " " + category.getName(), categoryFormat);
    }

    @Test
    public void testDateFormat() {
        final LocalDate localDate = LocalDate.of(2019, 3, 20);
        Assertions.assertEquals("Mar 20, 2019", formatter.format(localDate));

        user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, Locale.GERMAN);

        Assertions.assertEquals("20.03.2019", formatter.format(localDate));
    }
}

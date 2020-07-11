package org.financer.client.javafx.format

import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.user.User
import org.financer.client.format.Formatter
import org.financer.client.format.I18N.setLocalStorage
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.SettingPair
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

@Tag("unit")
class JavaFXFormatterTest {
    @BeforeEach
    fun init() {
        user = User()
        setLocalStorage(LocalStorageImpl.getInstance())
        user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, Locale.ENGLISH)
        user.putOrUpdateSettingProperty(SettingPair.Property.CURRENCY, Currency.getInstance("USD"))
        user.putOrUpdateSettingProperty(SettingPair.Property.SHOW_CURRENCY_SIGN, false)
        formatter = JavaFXFormatter(user)
        LocalStorageImpl.getInstance().writeObject("user", user)
    }

    @Test
    fun testCurrencyFormat() {
        val amount = Amount(5.387)
        var currencyFormat = formatter.format(amount)
        Assertions.assertEquals(String.format(Locale.ENGLISH, "%.2f", amount.amount) + " " + Currency.getInstance("USD").currencyCode, currencyFormat)
        user.putOrUpdateSettingProperty(SettingPair.Property.SHOW_CURRENCY_SIGN, true)
        currencyFormat = formatter.format(amount)
        Assertions.assertEquals(String.format(Locale.ENGLISH, "%.2f", amount.amount) + " " + Currency.getInstance("USD").symbol, currencyFormat)
        user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, Locale.GERMAN)
        currencyFormat = formatter.format(amount)
        Assertions.assertEquals(String.format(Locale.GERMAN, "%.2f", amount.amount).replace(".", ",") + " " +
                Currency.getInstance("USD").symbol, currencyFormat)
    }

    @Test
    fun testCategoryFormat() {
        val category = Category().setName("testCategory")
        val categoryFormat = formatter.format(category)
        Assertions.assertEquals(category.prefix + " " + category.name, categoryFormat)
    }

    @Test
    fun testDateFormat() {
        val localDate = LocalDate.of(2019, 3, 20)
        Assertions.assertEquals("Mar 20, 2019", formatter.format(localDate))
        user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, Locale.GERMAN)
        Assertions.assertEquals("20.03.2019", formatter.format(localDate))
    }

    companion object {
        private lateinit var user: User
        private lateinit var formatter: Formatter
    }
}
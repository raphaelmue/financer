package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Currency;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class SettingsTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);
    }

    @Test
    public void testChangeLanguage() {
        register(this.user, this.password);
        clickOn((Button) find("#settingTabBtn"));
        ComboBox<I18N.Language> languageComboBox = find("#languageMenuComboBox");
        clickOn(languageComboBox);
        clickOn(languageComboBox.getItems().get(1).getName());
        sleep(MEDIUM_SLEEP);

        User userToAssert = (User) LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertEquals(languageComboBox.getItems().get(1).getLocale(),
                userToAssert.getSettings().getLanguage());

        confirmDialog();
        sleep(MEDIUM_SLEEP);
        Assertions.assertNotNull(clickOn("Überblick"));
        clickOn((Button) find("#settingTabBtn"));
        languageComboBox = find("#languageMenuComboBox");
        Assertions.assertEquals(userToAssert.getSettings().getLanguage(),
                languageComboBox.getSelectionModel().getSelectedItem().getLocale());
    }

    @Test
    public void testChangeCurrency() {
        register(this.user, this.password);
        addCategory(category);
        addTransaction(transaction);
        clickOn((Button) find("#settingTabBtn"));
        ComboBox<Currency> currencyComboBox = find("#currencyComboBox");
        clickOn(currencyComboBox);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        User userToAssert = (User) LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertEquals(currencyComboBox.getSelectionModel().getSelectedItem(), userToAssert.getSettings().getCurrency());

        clickOn((Button) find("#transactionsTabBtn"));
        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        Assertions.assertNotNull(clickOn(String.format(userToAssert.getSettings().getLanguage(), "%.2f", transaction.getAmount()) +
                " " + userToAssert.getSettings().getCurrency().getCurrencyCode()));
    }

    @Test
    public void testChangeShowCurrencySign() {
        register(this.user, this.password);
        addCategory(category);
        addTransaction(transaction);

        sleep(SHORT_SLEEP);
        clickOn((Button) find("#settingTabBtn"));

        ComboBox<Currency> currencyComboBox = find("#currencyComboBox");
        CheckBox showCurrencySignCheckbox = find("#showSignCheckbox");

        clickOn(currencyComboBox);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        clickOn(showCurrencySignCheckbox);
        sleep(SHORT_SLEEP);
        User userToAssert = (User) LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertEquals(showCurrencySignCheckbox.isSelected(), userToAssert.getSettings().isShowCurrencySign());

        clickOn((Button) find("#transactionsTabBtn"));

        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        sleep(SHORT_SLEEP);

        Assertions.assertNotNull(clickOn(String.format(userToAssert.getSettings().getLanguage(), "%.2f", transaction.getAmount()) +
                " " + userToAssert.getSettings().getCurrency().getSymbol()));
    }

    @Test
    public void testChangeChangeAmountSignAutomatically() {
        register(this.user, this.password);

        clickOn((Button) find("#settingTabBtn"));
        clickOn("Transaction Settings");
        sleep(500);

        CheckBox changeAmountSignAutomaticallyCheckBox = find("#changeAmountSignAutomaticallyCheckBox");
        Assertions.assertFalse(changeAmountSignAutomaticallyCheckBox.isSelected());

        clickOn(changeAmountSignAutomaticallyCheckBox);

        addCategory(category);
        addTransaction(transaction);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(500);

        Assertions.assertNotNull(clickOn(formatter.formatCurrency(-transaction.getAmount())));

        Transaction insertedTransaction = (Transaction) LocalStorageImpl.getInstance().readList("transactions").get(0);
        Assertions.assertEquals(-transaction.getAmount(), insertedTransaction.getAmount());
    }
}

package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import de.raphaelmuesseler.financer.shared.model.user.User;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Currency;

@SuppressWarnings("WeakerAccess")
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
        Assertions.assertEquals(languageComboBox.getItems().get(1).getLocale(),
                ((LocalSettings) LocalStorageImpl.getInstance().readObject("localSettings")).getLanguage());
        confirmDialog();
        sleep(MEDIUM_SLEEP);
        Assertions.assertNotNull(clickOn("Überblick"));
        clickOn((Button) find("#settingTabBtn"));
        languageComboBox = find("#languageMenuComboBox");
        Assertions.assertEquals(((LocalSettings) LocalStorageImpl.getInstance().readObject("localSettings")).getLanguage(),
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
        Assertions.assertEquals(currencyComboBox.getItems().get(0), ((User) LocalStorageImpl.getInstance().readObject("user"))
                .getSettings().getCurrency());
        clickOn((Button) find("#transactionsTabBtn"));
        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        Assertions.assertNotNull(clickOn("-" +
                String.format(((LocalSettings) LocalStorageImpl.getInstance().readObject("localSettings")).getLanguage(), "%.2f", transaction.getAmount()) +
                " " + ((User) LocalStorageImpl.getInstance().readObject("user")).getSettings().getCurrency().getCurrencyCode()));
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
        Assertions.assertTrue(showCurrencySignCheckbox.isDisabled());

        clickOn(currencyComboBox);
        for (int i = 0; i < 63; i++) {
            press(KeyCode.DOWN).release(KeyCode.DOWN);
        }
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        clickOn(showCurrencySignCheckbox);
        sleep(SHORT_SLEEP);
        Assertions.assertEquals(showCurrencySignCheckbox.isSelected(), ((User) LocalStorageImpl.getInstance().readObject("user"))
                .getSettings().isShowCurrencySign());

        clickOn((Button) find("#transactionsTabBtn"));

        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        sleep(SHORT_SLEEP);
        Assertions.assertNotNull(clickOn("-" +
                String.format(((LocalSettings) LocalStorageImpl.getInstance().readObject("localSettings")).getLanguage(), "%.2f", transaction.getAmount()) +
                " " + ((User) LocalStorageImpl.getInstance().readObject("user")).getSettings().getCurrency().getSymbol()));
    }
}

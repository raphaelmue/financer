package org.financer.client.javafx.main;

import com.jfoenix.controls.JFXToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.category.CategoryRoot;
import org.financer.client.domain.model.transaction.Transaction;
import org.financer.client.domain.model.transaction.VariableTransaction;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.I18N;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.domain.model.value.objects.SettingPair;
import org.financer.util.collections.TreeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class SettingsTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testChangeLanguage() {
        register(user(), password());
        clickOn((Button) find("#settingTabBtn"));
        ComboBox<I18N.Language> languageComboBox = find("#languageMenuComboBox");
        clickOn(languageComboBox);
        clickOn(languageComboBox.getItems().get(1).getName());
        sleep(MEDIUM_SLEEP);

        User userToAssert = LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertEquals(languageComboBox.getItems().get(1).getLocale(),
                userToAssert.getValueOrDefault(SettingPair.Property.LANGUAGE));

        confirmDialog();
        sleep(MEDIUM_SLEEP);
        Assertions.assertNotNull(clickOn("Überblick"));
        clickOn((Button) find("#settingTabBtn"));
        languageComboBox = find("#languageMenuComboBox");
        Assertions.assertEquals(userToAssert.getValueOrDefault(SettingPair.Property.LANGUAGE),
                languageComboBox.getSelectionModel().getSelectedItem().getLocale());
    }

    @Test
    public void testChangeCurrency() {
        register(user(), password());
        addCategory(variableCategory());
        addVariableTransaction(variableTransaction());
        clickOn((Button) find("#settingTabBtn"));
        ComboBox<Currency> currencyComboBox = find("#currencyComboBox");
        clickOn(currencyComboBox);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        sleep(SHORT_SLEEP);
        User userToAssert = (User) LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertEquals(currencyComboBox.getSelectionModel().getSelectedItem(), userToAssert.getValueOrDefault(SettingPair.Property.CURRENCY));

        clickOn((Button) find("#transactionsTabBtn"));
        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        Assertions.assertNotNull(clickOn(String.format((Locale) userToAssert.getValueOrDefault(SettingPair.Property.LANGUAGE),
                "%.2f", variableTransaction().getAmount().getAmount()) +
                " " + ((Currency) userToAssert.getValueOrDefault(SettingPair.Property.CURRENCY)).getCurrencyCode()));
    }

    @Test
    public void testChangeShowCurrencySign() {
        final VariableTransaction transaction = variableTransaction();
        register(user(), password());
        addCategory(variableCategory());
        addVariableTransaction(transaction);
        clickOn((Button) find("#settingTabBtn"));

        clickOn((JFXToggleButton) find("#showSignCheckbox"));
        sleep(SHORT_SLEEP);
        User userToAssert = LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertTrue(userToAssert.getValueOrDefault(SettingPair.Property.SHOW_CURRENCY_SIGN));

        clickOn((Button) find("#transactionsTabBtn"));

        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        sleep(SHORT_SLEEP);

        Assertions.assertNotNull(clickOn(String.format((Locale) userToAssert.getValueOrDefault(SettingPair.Property.LANGUAGE),
                "%.2f", transaction.getAmount().getAmount()) +
                " " + ((Currency) userToAssert.getValueOrDefault(SettingPair.Property.CURRENCY)).getSymbol()));
    }

    @Test
    public void testChangeChangeAmountSignAutomatically() {
        register(user(), password());
        addCategory(variableCategory());
        final VariableTransaction transaction = variableTransaction();

        clickOn((Button) find("#settingTabBtn"));
        clickOn("Transaction Settings");
        sleep(SHORT_SLEEP);

        JFXToggleButton changeAmountSignAutomaticallyCheckBox = find("#changeAmountSignAutomaticallyCheckBox");
        Assertions.assertFalse(changeAmountSignAutomaticallyCheckBox.isSelected());

        clickOn(changeAmountSignAutomaticallyCheckBox);
        sleep(SHORT_SLEEP);
        User userToAssert = LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertTrue(userToAssert.getValueOrDefault(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY));

        addVariableTransaction(transaction);
        Assertions.assertNotNull(clickOn(formatter.format(transaction.getAmount().multiply(-1))));

        CategoryRoot categoryRoot = LocalStorageImpl.getInstance().readObject("categories");
        Set<Transaction> transactions = ((Category) TreeUtil.getByValue(categoryRoot, variableCategory(),
                Comparator.comparingLong(Category::getId))).getTransactions();
        Assertions.assertEquals(1, transactions.size());
        for (Transaction transactionToAssert : transactions) {
            Assertions.assertTrue(transactionToAssert instanceof VariableTransaction);
            Assertions.assertTrue(transactionToAssert.getId() > 0);
            Assertions.assertEquals(transaction.getAmount().multiply(-1), transactionToAssert.getAmount());
        }
    }
}

package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXToggleButton;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Comparator;
import java.util.Currency;
import java.util.Set;

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

        sleep(SHORT_SLEEP);
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
        clickOn((Button) find("#settingTabBtn"));

        clickOn((JFXToggleButton) find("#showSignCheckbox"));
        sleep(SHORT_SLEEP);
        User userToAssert = (User) LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertTrue(userToAssert.getSettings().isShowCurrencySign());

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
        addCategory(category);

        clickOn((Button) find("#settingTabBtn"));
        clickOn("Transaction Settings");
        sleep(SHORT_SLEEP);

        JFXToggleButton changeAmountSignAutomaticallyCheckBox = find("#changeAmountSignAutomaticallyCheckBox");
        Assertions.assertFalse(changeAmountSignAutomaticallyCheckBox.isSelected());

        clickOn(changeAmountSignAutomaticallyCheckBox);
        sleep(SHORT_SLEEP);
        User userToAssert = (User) LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertTrue(userToAssert.getSettings().isChangeAmountSignAutomatically());

        addTransaction(transaction);
        Assertions.assertNotNull(clickOn(formatter.formatCurrency(-transaction.getAmount())));

        BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
        Set<Transaction> transactions = ((CategoryTree) TreeUtil.getByValue(baseCategory, category,
                Comparator.comparingInt(Category::getId))).getTransactions();
        Assertions.assertEquals(1, transactions.size());
        for (Transaction transactionToAssert : transactions) {
            Assertions.assertTrue(transactionToAssert instanceof VariableTransaction);
            Assertions.assertTrue(transactionToAssert.getId() > 0);
            Assertions.assertEquals(-transaction.getAmount(), transactionToAssert.getAmount());
        }
    }
}

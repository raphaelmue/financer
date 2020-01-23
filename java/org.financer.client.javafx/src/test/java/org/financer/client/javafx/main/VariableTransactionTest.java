package org.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.model.categories.BaseCategory;
import org.financer.shared.model.categories.Category;
import org.financer.shared.model.categories.CategoryTree;
import org.financer.shared.model.transactions.Transaction;
import org.financer.shared.model.transactions.VariableTransaction;
import org.financer.util.collections.TreeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class VariableTransactionTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testAddTransaction() {
        register(this.user, this.password);
        addCategory(category);
        addTransaction(transaction);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(1000);

        Assertions.assertNotNull(clickOn(formatter.formatCurrency(transaction.getAmount())));

        BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
        Set<Transaction> transactions = ((CategoryTree) TreeUtil.getByValue(baseCategory, category,
                Comparator.comparingInt(Category::getId))).getTransactions();
        Assertions.assertEquals(1, transactions.size());
        for (Transaction transaction : transactions) {
            Assertions.assertTrue(transaction instanceof VariableTransaction);
            Assertions.assertTrue(transaction.getId() > 0);
        }
    }

    @Test
    public void testEditTransaction() {
        register(this.user, this.password);
        addCategory(category);
        addTransaction(transaction);
        clickOn(transaction.getProduct());
        clickOn((Button) find("#editTransactionBtn"));
        final double amount = transaction.getAmount() / 2;
        TextField amountTextField = find("#amountTextField");
        clickOn(amountTextField);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        write(Double.toString(amount));
        confirmDialog();
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(MEDIUM_SLEEP);

        Assertions.assertNotNull(clickOn(formatter.formatCurrency(amount)));
        BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
        Set<Transaction> transactions = ((CategoryTree) TreeUtil.getByValue(baseCategory, category,
                Comparator.comparingInt(Category::getId))).getTransactions();
        Assertions.assertEquals(1, transactions.size());
        for (Transaction transaction : transactions) {
            Assertions.assertTrue(transaction instanceof VariableTransaction);
            Assertions.assertTrue(transaction.getId() > 0);
            Assertions.assertEquals(amount, transaction.getAmount());
        }
    }

    @Test
    public void testDeleteTransaction() {
        register(this.user, this.password);
        addCategory(this.category);
        addTransaction(this.transaction);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(MEDIUM_SLEEP);
        clickOn(transaction.getProduct());
        clickOn((JFXButton) find("#deleteTransactionBtn"));
        confirmDialog();
        sleep(MEDIUM_SLEEP);
        Assertions.assertEquals(0, ((TableView) find("#transactionsTableView")).getItems().size());
        BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
        Assertions.assertEquals(0, ((CategoryTree) TreeUtil.getByValue(baseCategory, transaction.getCategoryTree(),
                Comparator.comparingInt(Category::getId))).getTransactions().size());
    }
}

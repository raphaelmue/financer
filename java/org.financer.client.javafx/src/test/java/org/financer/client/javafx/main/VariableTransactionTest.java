package org.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.category.CategoryRoot;
import org.financer.client.domain.model.transaction.Transaction;
import org.financer.client.domain.model.transaction.VariableTransaction;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.domain.model.value.objects.Amount;
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
        final VariableTransaction transaction = variableTransaction();
        register(user(), password());
        addCategory(variableCategory());
        addVariableTransaction(transaction);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(1000);

        Assertions.assertNotNull(clickOn(formatter.format(transaction.getAmount())));

        CategoryRoot categoryRoot = LocalStorageImpl.getInstance().readObject("categories");
        Set<Transaction> transactions = ((Category) TreeUtil.getByValue(categoryRoot, transaction.getCategory(),
                Comparator.comparingLong(Category::getId))).getTransactions();
        Assertions.assertEquals(1, transactions.size());
        for (Transaction _transaction : transactions) {
            Assertions.assertTrue(_transaction instanceof VariableTransaction);
            Assertions.assertTrue(_transaction.getId() > 0);
        }
    }

    @Test
    public void testEditTransaction() {
        final VariableTransaction transaction = variableTransaction();
        register(user(), password());
        addCategory(variableCategory());
        addVariableTransaction(transaction);
        clickOn(formatter.format(transaction.getValueDate()));
        clickOn((Button) find("#editTransactionBtn"));
        final Amount amount = transaction.getAmount().divide(2);
        TextField amountTextField = find("#amountTextField");
        clickOn(amountTextField);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        write(Double.toString(amount.getAmount()));
        confirmDialog();
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(MEDIUM_SLEEP);

        Assertions.assertNotNull(clickOn(formatter.format(amount)));
        CategoryRoot categoryRoot = LocalStorageImpl.getInstance().readObject("categories");
        Set<Transaction> transactions = ((Category) TreeUtil.getByValue(categoryRoot, transaction.getCategory(),
                Comparator.comparingLong(Category::getId))).getTransactions();
        Assertions.assertEquals(1, transactions.size());
        for (Transaction _transaction : transactions) {
            Assertions.assertTrue(_transaction instanceof VariableTransaction);
            Assertions.assertTrue(_transaction.getId() > 0);
            Assertions.assertEquals(amount, _transaction.getAmount());
        }
    }

    @Test
    public void testDeleteTransaction() {
        final VariableTransaction transaction = variableTransaction();
        register(user(), password());
        addCategory(variableCategory());
        addVariableTransaction(transaction);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(MEDIUM_SLEEP);
        clickOn(formatter.format(transaction.getValueDate()));
        clickOn((JFXButton) find("#deleteTransactionBtn"));
        confirmDialog();
        sleep(MEDIUM_SLEEP);
        Assertions.assertEquals(0, ((TableView) find("#transactionsTableView")).getItems().size());
        CategoryRoot categoryRoot = LocalStorageImpl.getInstance().readObject("categories");
        Assertions.assertEquals(0, ((Category) TreeUtil.getByValue(categoryRoot, transaction.getCategory(),
                Comparator.comparingLong(Category::getId))).getTransactions().size());
    }
}

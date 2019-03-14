package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Comparator;

public class TransactionTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);
    }

    @Test
    public void testAddTransaction() {
        register(this.user, this.password);
        addCategory(category);
        addTransaction(transaction);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(500);

        Assertions.assertNotNull(clickOn(formatter.formatCurrency(transaction.getAmount())));
        Assertions.assertEquals(1, LocalStorageImpl.getInstance().readList("transactions").size());

        Transaction insertedTransaction = (Transaction) LocalStorageImpl.getInstance().readList("transactions").get(0);
        Assertions.assertEquals(transaction.getAmount(), insertedTransaction.getAmount());
        Assertions.assertEquals(transaction.getProduct(), insertedTransaction.getProduct());
        Assertions.assertEquals(transaction.getValueDate(), insertedTransaction.getValueDate());
        Assertions.assertEquals(transaction.getPurpose(), insertedTransaction.getPurpose());
        Assertions.assertEquals(transaction.getShop(), insertedTransaction.getShop());
        Assertions.assertEquals(transaction.getCategoryTree().getValue().getName(), insertedTransaction.getCategoryTree().getValue().getName());
    }

    @Test
    public void testEditTransaction() {
        register(this.user, this.password);
        addCategory(category);
        addTransaction(transaction);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(500);

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
        sleep(500);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(500);

        // uncomment when issue is fixed
        System.out.println(formatter.formatCurrency(amount));
        Assertions.assertNotNull(clickOn(formatter.formatCurrency(amount)));
        Assertions.assertEquals(1, LocalStorageImpl.getInstance().readList("transactions").size());

        Transaction insertedTransaction = (Transaction) LocalStorageImpl.getInstance().readList("transactions").get(0);
        Assertions.assertEquals(transaction.getAmount() / 2, insertedTransaction.getAmount());
    }

    @Test
    public void testDeleteTransaction() {
        register(this.user, this.password);
        addCategory(this.category);
        addTransaction(this.transaction);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(500);

        clickOn(transaction.getProduct());
        clickOn((JFXButton) find("#deleteTransactionBtn"));
        confirmDialog();

        sleep(500);
        Assertions.assertEquals(0, ((TableView) find("#transactionsTableView")).getItems().size());

        BaseCategory baseCategory = (BaseCategory) LocalStorageImpl.getInstance().readObject("categories");
        Assertions.assertEquals(0, ((CategoryTree)TreeUtil.getByValue(baseCategory, transaction.getCategoryTree(),
                Comparator.comparingInt(Category::getId))).getTransactions().size());
    }
}

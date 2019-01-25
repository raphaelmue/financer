package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

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

        Assertions.assertNotNull(clickOn("-" + Formatter.formatCurrency(transaction.getAmount())));
        Assertions.assertEquals(1, LocalStorageImpl.getInstance().readList("transactions").size());

        Transaction insertedTransaction = (Transaction) LocalStorageImpl.getInstance().readList("transactions").get(0);
        Assertions.assertEquals(-transaction.getAmount(), insertedTransaction.getAmount());
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

        clickOn("OK");
        sleep(500);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(500);

        // uncomment when issue is fixed
        Assertions.assertNotNull(clickOn("-" + Formatter.formatCurrency(amount)));
        Assertions.assertEquals(1, LocalStorageImpl.getInstance().readList("transactions").size());

        Transaction insertedTransaction = (Transaction) LocalStorageImpl.getInstance().readList("transactions").get(0);
        Assertions.assertEquals(-transaction.getAmount() / 2, insertedTransaction.getAmount());
    }
}

package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.components.DoubleField;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.transactions.AbstractTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

@SuppressWarnings("WeakerAccess")
public class FixedTransactionTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);
    }

    @Test
    public void testAddFixedTransaction() {
        register(user, password);
        category.setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        sleep(500);
        // TODO: related to issue #14
        // clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategoryTree().getValue().getName())));
        // sleep(500);
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("active"))));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(formatter.formatCurrency(fixedTransaction.getAmount()))));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("since") + " " +
                fixedTransaction.getStartDate().toString())));
    }

    @Test
    public void testEditFixedTransaction() {
        register(user, password);
        category.setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        final double amount = fixedTransaction.getAmount() / 2;
        sleep(500);
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        clickOn(find((Label label) -> label.getText().contains(I18N.get("active"))));
        clickOn((JFXButton) find("#editFixedTransactionBtn"));
        clickOn((DoubleField) find("#amountTextField"));
        eraseText(6);
        write(Double.toString(amount));
        confirmDialog();
        sleep(500);
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(formatter.formatCurrency(amount))));
        Assertions.assertEquals(1, getCategoryTree().getTransactions().size());
    }

    @Test
    public void testDeleteFixedTransaction() {
        register(user, password);
        category.setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        sleep(500);
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        clickOn(find((Label label) -> label.getText().contains(I18N.get("active"))));
        clickOn((JFXButton) find("#deleteFixedTransactionBtn"));
        confirmDialog();
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        Assertions.assertEquals(0, ((JFXListView) find("#fixedTransactionsListView")).getItems().size());
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(formatter.formatCurrency(0.0))));
    }


    // ------------------ VARIABLE ------------------ \\

    @Test
    public void testAddFixedVariableTransaction() {
        fixedTransaction.setVariable(true);
        fixedTransaction.getTransactionAmounts().add(new TransactionAmount(-1, 450.0, LocalDate.now().withDayOfMonth(1)));
        register(user, password);
        category.setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        sleep(500);
        clickOn((JFXButton) find("#refreshFixedTransactionsBtn"));
        sleep(500);
        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategoryTree().getValue().getName())));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("active"))));
        Assertions.assertEquals(450.0, fixedTransaction.getTransactionAmounts().get(0).getAmount());
        // TODO: works locally, but not on server; needs to be fixed
        // Assertions.assertNotNull(find((Label label) -> label.getText().contains(Formatter.formatCurrency(
        //        fixedTransaction.getTransactionAmounts().get(0).getAmount()))));
    }

    @Test
    public void testEditFixedVariableTransaction() {
        final double amount = 450.0;
        fixedTransaction.setVariable(true);
        fixedTransaction.getTransactionAmounts().add(new TransactionAmount(-1, amount, LocalDate.now().withDayOfMonth(1)));
        fixedTransaction.getTransactionAmounts().add(new TransactionAmount(-1, amount, LocalDate.now().minusMonths(1).withDayOfMonth(1)));
        register(user, password);
        category.setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        category.getValue().setPrefix("3.1");
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        sleep(1000);
        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategoryTree().getValue().getName())));
        clickOn(find((Label label) -> label.getText().contains(I18N.get("active"))));
        clickOn((JFXButton) find("#editFixedTransactionBtn"));
        sleep(1000);
        Assertions.assertTrue(find("#editTransactionAmountBtn").isDisabled());
        Assertions.assertTrue(find("#deleteTransactionAmountBtn").isDisabled());
        clickOn(formatter.formatDate(fixedTransaction.getTransactionAmounts().get(0).getValueDate()));
        clickOn((JFXButton) find("#deleteTransactionAmountBtn"));
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        JFXListView<TransactionAmount> transactionAmountListView = find("#transactionAmountListView");
        Assertions.assertEquals(1, transactionAmountListView.getItems().size());
        clickOn(formatter.formatDate(fixedTransaction.getTransactionAmounts().get(1).getValueDate()));
        clickOn((JFXButton) find("#editTransactionAmountBtn"));
        clickOn((DoubleField) find("#transactionAmountTextField"));
        eraseText(6);
        write(Double.toString(amount / 2));
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        Assertions.assertNotNull(clickOn(formatter.formatCurrency(amount / 2)));
        confirmDialog();
        sleep(1000);

        Assertions.assertEquals(1, getCategoryTree().getTransactions().size());
        FixedTransaction updatedTransaction = null;
        for (AbstractTransaction transaction : getCategoryTree().getTransactions()) {
            updatedTransaction = (FixedTransaction) transaction;
            break;
        }
        Assertions.assertNotNull(updatedTransaction);
        Assertions.assertEquals(amount / 2, updatedTransaction.getTransactionAmounts().get(0).getAmount());
    }
}

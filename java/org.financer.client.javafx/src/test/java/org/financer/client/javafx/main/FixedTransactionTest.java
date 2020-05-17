package org.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import org.financer.client.domain.model.transaction.FixedTransaction;
import org.financer.client.domain.model.transaction.FixedTransactionAmount;
import org.financer.client.domain.model.transaction.Transaction;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.DoubleField;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class FixedTransactionTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testAddFixedTransaction() {
        FixedTransaction fixedTransaction = fixedTransaction();
        register(user(), password());
        addCategory(fixedCategory());
        addFixedTransaction(fixedTransaction);
        sleep(SHORT_SLEEP);
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("active"))));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(formatter.format(fixedTransaction.getAmount()))));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("since") + " " +
                formatter.format(fixedTransaction.getTimeRange().getStartDate()))));
    }

    @Test
    public void testEditFixedTransaction() {
        FixedTransaction fixedTransaction = fixedTransaction();
        register(user(), password());
        addCategory(fixedCategory());
        addFixedTransaction(fixedTransaction);
        final Amount amount = fixedTransaction.getAmount().divide(2);
        sleep(SHORT_SLEEP);
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        clickOn(find((Label label) -> label.getText().contains(I18N.get("active"))));
        clickOn((JFXButton) find("#editFixedTransactionBtn"));
        clickOn((DoubleField) find("#amountTextField"));
        eraseText(6);
        write(Double.toString(amount.getAmount()));
        confirmDialog();
        sleep(SHORT_SLEEP);
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(formatter.format(amount))));
//        Assertions.assertEquals(1, ((Category) TreeUtil.getByValue(((CategoryRoot) LocalStorageImpl.getInstance().readObject("categories"))
//                        .getCategoriesByClass(fixedTransaction.getCategoryTree().getValue().getCategoryClass()),
//                fixedTransaction.getCategoryTree(), (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()))).getTransactions().size());
    }

    @Test
    public void testDeleteFixedTransaction() {
        register(user(), password());
        addCategory(fixedCategory());
        addFixedTransaction(fixedTransaction());
        sleep(SHORT_SLEEP);
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        clickOn(find((Label label) -> label.getText().contains(I18N.get("active"))));
        clickOn((JFXButton) find("#deleteFixedTransactionBtn"));
        confirmDialog();
        sleep(SHORT_SLEEP);
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        Assertions.assertEquals(0, ((JFXListView) find("#fixedTransactionsListView")).getItems().size());
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(formatter.format(new Amount()))));
    }


    // ------------------ VARIABLE ------------------ \\

    @Test
    public void testAddFixedVariableTransaction() {
        FixedTransaction fixedTransaction = fixedTransaction();
        fixedTransaction.setIsVariable(true);
        register(user(), password());
        addCategory(fixedCategory());
        addFixedTransaction(fixedTransaction);
        sleep(SHORT_SLEEP);
        clickOn((JFXButton) find("#refreshFixedTransactionsBtn"));
        sleep(SHORT_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategory().getName())));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("active"))));
        Assertions.assertEquals(1, fixedTransaction.getTransactionAmounts().size());
        for (FixedTransactionAmount transactionAmount : fixedTransaction.getTransactionAmounts()) {
            Assertions.assertEquals(450.0, transactionAmount.getAmount().getAmount());
        }
        // TODO: works locally, but not on server; needs to be fixed
        // Assertions.assertNotNull(find((Label label) -> label.getText().contains(Formatter.formatCurrency(
        //        fixedTransaction.getTransactionAmounts().get(0).getAmount()))));
    }

    @Test
    @Tag("skip")
    public void testEditFixedVariableTransactionEditTransactionAmount() {
        final double amount = 450.0;
        FixedTransaction fixedTransaction = fixedTransaction();
        fixedTransaction.setIsVariable(true);
        register(user(), password());
        addCategory(fixedCategory());
        addFixedTransaction(fixedTransaction);
        sleep(MEDIUM_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategory().getName())));
        sleep(SHORT_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(I18N.get("active"))));
        clickOn((JFXButton) find("#editFixedTransactionBtn"));
        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#editTransactionAmountBtn").isDisabled());
        Assertions.assertTrue(find("#deleteTransactionAmountBtn").isDisabled());
        Assertions.assertTrue(((CheckBox) find("#isVariableCheckbox")).isSelected());
        JFXListView<FixedTransactionAmount> transactionAmountListView = find("#transactionAmountListView");
        Assertions.assertTrue(transactionAmountListView.isManaged());
        transactionAmountListView.getSelectionModel().select(0);
        clickOn((JFXButton) find("#editTransactionAmountBtn"));
        clickOn((DoubleField) find("#transactionAmountTextField"));
        eraseText(6);
        write(Double.toString(amount / 2));
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        Assertions.assertNotNull(clickOn(formatter.format(new Amount(amount / 2))));
        confirmDialog();
        sleep(MEDIUM_SLEEP);
        FixedTransaction updatedTransaction;
        for (Transaction transaction : getCategoryTree().getTransactions()) {
            if (transaction instanceof FixedTransaction) {
                updatedTransaction = (FixedTransaction) transaction;
                Assertions.assertNotNull(updatedTransaction);
                Assertions.assertEquals(amount / 2, updatedTransaction.getAmount(new ValueDate()).getAmount());
            }
        }
    }

    @Test
    @Tag("skip")
    public void testEditFixedVariableTransactionDeleteTransactionAmount() {
        FixedTransaction fixedTransaction = fixedTransaction();
        fixedTransaction.setIsVariable(true);
        register(user(), password());
        addCategory(fixedCategory());
        addFixedTransaction(fixedTransaction);
        sleep(MEDIUM_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategory().getName())));
        sleep(SHORT_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(I18N.get("active"))));
        clickOn((JFXButton) find("#editFixedTransactionBtn"));
        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#editTransactionAmountBtn").isDisabled());
        Assertions.assertTrue(find("#deleteTransactionAmountBtn").isDisabled());
        Assertions.assertTrue(((CheckBox) find("#isVariableCheckbox")).isSelected());
        JFXListView<FixedTransactionAmount> transactionAmountListView = find("#transactionAmountListView");
        Assertions.assertTrue(transactionAmountListView.isManaged());
        transactionAmountListView.getSelectionModel().select(0);
        clickOn((JFXButton) find("#deleteTransactionAmountBtn"));
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        sleep(MEDIUM_SLEEP);
        transactionAmountListView = find("#transactionAmountListView");
        Assertions.assertEquals(0, transactionAmountListView.getItems().size());
        confirmDialog();
        sleep(MEDIUM_SLEEP);
        FixedTransaction updatedTransaction;
        for (Transaction transaction : getCategoryTree().getTransactions()) {
            if (transaction instanceof FixedTransaction) {
                updatedTransaction = (FixedTransaction) transaction;
                Assertions.assertNotNull(updatedTransaction);
                Assertions.assertEquals(0, updatedTransaction.getTransactionAmounts().size());
            }
        }
    }
}

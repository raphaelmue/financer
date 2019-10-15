package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.components.DoubleField;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class FixedTransactionTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testAddFixedTransaction() {
        fixedTransaction.setProduct("Test Product");
        fixedTransaction.setPurpose("Test Purpose");
        register(user, password);
        category.getValue().setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        sleep(SHORT_SLEEP);
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("active"))));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(formatter.formatCurrency(fixedTransaction.getAmount()))));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("since") + " " +
                formatter.formatDate(fixedTransaction.getStartDate()))));
    }

    @Test
    public void testEditFixedTransaction() {
        register(user, password);
        category.getValue().setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        final double amount = fixedTransaction.getAmount() / 2;
        sleep(SHORT_SLEEP);
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
        sleep(SHORT_SLEEP);
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(formatter.formatCurrency(amount))));
        Assertions.assertEquals(1, ((CategoryTree) TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories"))
                        .getCategoryTreeByCategoryClass(fixedTransaction.getCategoryTree().getValue().getCategoryClass()),
                fixedTransaction.getCategoryTree(), (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()))).getTransactions().size());
    }

    @Test
    public void testDeleteFixedTransaction() {
        register(user, password);
        category.getValue().setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);
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
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(formatter.formatCurrency(0.0))));
    }


    // ------------------ VARIABLE ------------------ \\

    @Test
    public void testAddFixedVariableTransaction() {
        fixedTransaction.setIsVariable(true);
        fixedTransaction.getTransactionAmounts().add(new TransactionAmount(-1, 450.0, LocalDate.now().withDayOfMonth(1)));
        register(user, password);
        category.getValue().setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        sleep(SHORT_SLEEP);
        clickOn((JFXButton) find("#refreshFixedTransactionsBtn"));
        sleep(SHORT_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategoryTree().getValue().getName())));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("active"))));
        Assertions.assertEquals(1, fixedTransaction.getTransactionAmounts().size());
        for (TransactionAmount transactionAmount : fixedTransaction.getTransactionAmounts()) {
            Assertions.assertEquals(450.0, transactionAmount.getAmount());
        }
        // TODO: works locally, but not on server; needs to be fixed
        // Assertions.assertNotNull(find((Label label) -> label.getText().contains(Formatter.formatCurrency(
        //        fixedTransaction.getTransactionAmounts().get(0).getAmount()))));
    }

    @Test
    @Tag("skip")
    public void testEditFixedVariableTransactionEditTransactionAmount() {
        final double amount = 450.0;
        fixedTransaction.setIsVariable(true);
        fixedTransaction.getTransactionAmounts().add(new TransactionAmount(-1, amount, LocalDate.now().withDayOfMonth(1)));
        register(user, password);
        category.getValue().setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        category.getValue().setPrefix("3.1");
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        sleep(MEDIUM_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategoryTree().getValue().getName())));
        sleep(SHORT_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(I18N.get("active"))));
        clickOn((JFXButton) find("#editFixedTransactionBtn"));
        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#editTransactionAmountBtn").isDisabled());
        Assertions.assertTrue(find("#deleteTransactionAmountBtn").isDisabled());
        Assertions.assertTrue(((CheckBox) find("#isVariableCheckbox")).isSelected());
        JFXListView<TransactionAmount> transactionAmountListView = find("#transactionAmountListView");
        Assertions.assertTrue(transactionAmountListView.isManaged());
        transactionAmountListView.getSelectionModel().select(0);
        clickOn((JFXButton) find("#editTransactionAmountBtn"));
        clickOn((DoubleField) find("#transactionAmountTextField"));
        eraseText(6);
        write(Double.toString(amount / 2));
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        Assertions.assertNotNull(clickOn(formatter.formatCurrency(amount / 2)));
        confirmDialog();
        sleep(MEDIUM_SLEEP);
        FixedTransaction updatedTransaction;
        for (Transaction transaction : getCategoryTree().getTransactions()) {
            if (transaction instanceof FixedTransaction) {
                updatedTransaction = (FixedTransaction) transaction;
                Assertions.assertNotNull(updatedTransaction);
                Assertions.assertEquals(amount / 2, updatedTransaction.getAmount(LocalDate.now()));
            }
        }
    }

    @Test
    @Tag("skip")
    public void testEditFixedVariableTransactionDeleteTransactionAmount() {
        fixedTransaction.setIsVariable(true);
        fixedTransaction.getTransactionAmounts().add(new TransactionAmount(-1, 450.0, LocalDate.now().withDayOfMonth(1)));
        register(user, password);
        category.getValue().setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        category.getValue().setPrefix("3.1");
        addCategory(category);
        addFixedTransaction(fixedTransaction);
        sleep(MEDIUM_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategoryTree().getValue().getName())));
        sleep(SHORT_SLEEP);
        clickOn(find((Label label) -> label.getText().contains(I18N.get("active"))));
        clickOn((JFXButton) find("#editFixedTransactionBtn"));
        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#editTransactionAmountBtn").isDisabled());
        Assertions.assertTrue(find("#deleteTransactionAmountBtn").isDisabled());
        Assertions.assertTrue(((CheckBox) find("#isVariableCheckbox")).isSelected());
        JFXListView<TransactionAmount> transactionAmountListView = find("#transactionAmountListView");
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

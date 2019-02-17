package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.components.DoubleField;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

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
        Assertions.assertEquals(1, ((CategoryTree) TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories"))
                        .getCategoryTreeByCategoryClass(fixedTransaction.getCategoryTree().getCategoryClass()),
                fixedTransaction.getCategoryTree(), (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()))).getTransactions().size());
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
        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);

        sleep(500);

        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("active"))));
        Assertions.assertEquals(450.0, fixedTransaction.getTransactionAmounts().get(0).getAmount());
        // TODO: works locally, but not on server; needs to be fixed
        // Assertions.assertNotNull(find((Label label) -> label.getText().contains(Formatter.formatCurrency(
        //        fixedTransaction.getTransactionAmounts().get(0).getAmount()))));
    }
}

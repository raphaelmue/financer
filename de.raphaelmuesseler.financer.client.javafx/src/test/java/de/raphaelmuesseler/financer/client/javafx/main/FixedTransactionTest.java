package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

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
        Assertions.assertNotNull(find((Label label) -> label.getText().contains((-fixedTransaction.getAmount() + "0")
                .replace(".", ","))));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("since") + " " +
                fixedTransaction.getStartDate().toString())));
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
        Assertions.assertNotNull(find((Label label) -> label.getText().contains("0,00")));
    }


    // ------------------ VARIABLE ------------------ \\

    @Test
    public void testAddFixedVariableTransaction() {
        fixedTransaction.setVariable(true);
        fixedTransaction.getTransactionAmounts().add(new TransactionAmount(-1, 450.0, fixedTransaction.getStartDate().withDayOfMonth(1)));

        register(user, password);
        category.setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);

        sleep(500);

        clickOn((JFXListView) find("#categoriesListView"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.DOWN).release(KeyCode.DOWN);

        sleep(500);

        Assertions.assertNotNull(find((Label label) -> label.getText().contains(I18N.get("active"))));
        Assertions.assertNotNull(find((Label label) -> label.getText().contains((fixedTransaction.getTransactionAmounts().get(0).getAmount() + "0")
                .replace(".", ","))));
    }
}

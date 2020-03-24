package org.financer.client.javafx.main;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.DoubleField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class OverviewTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testLastTransactions() {
        register(user, password);
        Assertions.assertNotNull(clickOn(I18N.get("noRecentTransactions")));
        transaction.setValueDate(LocalDate.now().withDayOfMonth(1));
        addCategory(category);
        addTransaction(transaction);

        clickOn((Button) find("#overviewTabBtn"));
        sleep(SHORT_SLEEP);

        GridPane gridPane = find("#lastTransactionsGridPane");
        Assertions.assertEquals(2, gridPane.getChildren().size());
    }

    @Test
    public void testUpcomingFixedTransactions() {
        register(user, password);
        Assertions.assertNotNull(clickOn(I18N.get("noUpcomingTransactions")));
        category.getValue().setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        fixedTransaction.setIsVariable(true);
        addCategory(category);
        addFixedTransaction(fixedTransaction);

        clickOn((Button) find("#overviewTabBtn"));
        sleep(SHORT_SLEEP);

        GridPane gridPane = find("#upcomingFixedTransactionGridPane");
        // 2 children because of 2 columns in grid pane
        Assertions.assertEquals(2, gridPane.getChildren().size());

        clickOn(gridPane.getChildren().get(0));
        sleep(SHORT_SLEEP);

        clickOn((DoubleField) find("#transactionAmountTextField"));
        eraseText(4).write("500.0");
        confirmDialog();
        sleep(SHORT_SLEEP);

        gridPane = find("#upcomingFixedTransactionGridPane");
        // 1 since placeholder label
        Assertions.assertEquals(1, gridPane.getChildren().size());
        Assertions.assertEquals(500.0, getCategoryTree().getAmount(LocalDate.now()));
    }
}

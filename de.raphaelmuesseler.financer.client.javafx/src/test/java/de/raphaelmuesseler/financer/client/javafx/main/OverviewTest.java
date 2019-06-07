package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.components.DoubleField;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class OverviewTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);
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

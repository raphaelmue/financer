package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import javafx.geometry.VerticalDirection;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class StatisticsTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);
    }

    @Test
    public void testFixedExpensesDistributionChart() {
        register(user, password);
        category.getValue().setCategoryClass(BaseCategory.CategoryClass.FIXED_EXPENSES);
        addCategory(category);
        addFixedTransaction(fixedTransaction);

        clickOn((Button) find("#statisticsTabBtn"));
        sleep(SHORT_SLEEP);

        clickOn((ScrollPane) find("#statisticsScrollPane"));
        scroll(50, VerticalDirection.DOWN);

        Assertions.assertNotNull(clickOn(new JavaFXFormatter(LocalStorageImpl.getInstance()).formatCategoryName(category)));

        JFXDatePicker fromDatePicker = find("#fixedExpensesFromDatePicker");
        fromDatePicker.setValue(fixedTransaction.getStartDate().minusMonths(5));

        JFXDatePicker toDatePicker = find("#fixedExpensesToDatePicker");
        toDatePicker.setValue(fixedTransaction.getStartDate().minusMonths(4));

        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#fixedExpensesNoDataLabel").isVisible());
    }

    @Test
    public void testVariableExpensesDistributionChart() {
        register(user, password);
        transaction.setValueDate(LocalDate.now().withDayOfMonth(1));
        addCategory(category);
        addTransaction(transaction);

        clickOn((Button) find("#statisticsTabBtn"));
        sleep(SHORT_SLEEP);

        clickOn((ScrollPane) find("#statisticsScrollPane"));
        scroll(50, VerticalDirection.DOWN);

        Assertions.assertNotNull(clickOn(new JavaFXFormatter(LocalStorageImpl.getInstance()).formatCategoryName(category)));

        JFXDatePicker fromDatePicker = find("#variableExpensesFromDatePicker");
        fromDatePicker.setValue(transaction.getValueDate().minusMonths(5));

        JFXDatePicker toDatePicker = find("#variableExpensesToDatePicker");
        toDatePicker.setValue(transaction.getValueDate().minusMonths(4));

        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#variableExpensesNoDataLabel").isVisible());

    }

}

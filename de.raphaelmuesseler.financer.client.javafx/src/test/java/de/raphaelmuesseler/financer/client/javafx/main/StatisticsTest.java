package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import javafx.geometry.VerticalDirection;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class StatisticsTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testProgressChart() {
        register(user, password);
        transaction.setValueDate(LocalDate.now().withDayOfMonth(1));
        addCategory(category);
        addTransaction(transaction);

        clickOn((Button) find("#statisticsTabBtn"));
        sleep(SHORT_SLEEP);

        AreaChart chart = find("#progressLineChart");
        Assertions.assertEquals(1, chart.getData().size());

        clickOn((ComboBox) find("#progressChartDefaultCategoryComboBox"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        clickOn((JFXButton) find("#addCategoryBtn"));
        VBox categoriesContainer = find("#categoriesContainer");
        HBox dropDownHBox = (HBox) categoriesContainer.getChildren().get(1);
        JFXComboBox<CategoryTree> categoryComboBox = (JFXComboBox<CategoryTree>) dropDownHBox.getChildren().get(0);
        clickOn(categoryComboBox);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        sleep(SHORT_SLEEP);
        chart = find("#progressLineChart");
        Assertions.assertEquals(2, chart.getData().size());
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

        PieChart pieChart = find("#fixedExpensesDistributionChart");
        Assertions.assertEquals(1, pieChart.getData().size());
        Assertions.assertFalse(find("#fixedExpensesNoDataLabel").isVisible());

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

        PieChart pieChart = find("#variableExpensesDistributionChart");
        Assertions.assertEquals(1, pieChart.getData().size());
        Assertions.assertFalse(find("#variableExpensesNoDataLabel").isVisible());

        JFXDatePicker fromDatePicker = find("#variableExpensesFromDatePicker");
        fromDatePicker.setValue(transaction.getValueDate().minusMonths(5));

        JFXDatePicker toDatePicker = find("#variableExpensesToDatePicker");
        toDatePicker.setValue(transaction.getValueDate().minusMonths(4));

        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#variableExpensesNoDataLabel").isVisible());
    }

}

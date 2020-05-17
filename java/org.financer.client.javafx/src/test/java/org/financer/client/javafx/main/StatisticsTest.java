package org.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import javafx.geometry.VerticalDirection;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.transaction.FixedTransaction;
import org.financer.client.domain.model.transaction.VariableTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class StatisticsTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testProgressChart() {
        final VariableTransaction transaction = variableTransaction();
        register(user(), password());
        addCategory(variableCategory());
        addVariableTransaction(transaction);

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
        JFXComboBox<Category> categoryComboBox = (JFXComboBox<Category>) dropDownHBox.getChildren().get(0);
        clickOn(categoryComboBox);
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        sleep(SHORT_SLEEP);
        chart = find("#progressLineChart");
        Assertions.assertEquals(2, chart.getData().size());
    }

    @Test
    public void testFixedExpensesDistributionChart() {
        final FixedTransaction transaction = fixedTransaction();
        register(user(), password());
        addCategory(fixedCategory());
        addFixedTransaction(transaction);

        clickOn((Button) find("#statisticsTabBtn"));
        sleep(SHORT_SLEEP);

        clickOn((ScrollPane) find("#statisticsScrollPane"));
        scroll(50, VerticalDirection.DOWN);

        PieChart pieChart = find("#fixedExpensesDistributionChart");
        Assertions.assertEquals(1, pieChart.getData().size());
        Assertions.assertFalse(find("#fixedExpensesNoDataLabel").isVisible());

        JFXDatePicker fromDatePicker = find("#fixedExpensesFromDatePicker");
        fromDatePicker.setValue(transaction.getTimeRange().getStartDate().minusMonths(5));

        JFXDatePicker toDatePicker = find("#fixedExpensesToDatePicker");
        toDatePicker.setValue(transaction.getTimeRange().getStartDate().minusMonths(4));

        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#fixedExpensesNoDataLabel").isVisible());
    }

    @Test
    public void testVariableExpensesDistributionChart() {
        final VariableTransaction transaction = variableTransaction();
        register(user(), password());
        addCategory(variableCategory());
        addVariableTransaction(transaction);

        clickOn((Button) find("#statisticsTabBtn"));
        sleep(SHORT_SLEEP);

        clickOn((ScrollPane) find("#statisticsScrollPane"));
        scroll(50, VerticalDirection.DOWN);

        PieChart pieChart = find("#variableExpensesDistributionChart");
        Assertions.assertEquals(1, pieChart.getData().size());
        Assertions.assertFalse(find("#variableExpensesNoDataLabel").isVisible());

        JFXDatePicker fromDatePicker = find("#variableExpensesFromDatePicker");
        fromDatePicker.setValue(transaction.getValueDate().getDate().minusMonths(5));

        JFXDatePicker toDatePicker = find("#variableExpensesToDatePicker");
        toDatePicker.setValue(transaction.getValueDate().getDate().minusMonths(4));

        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#variableExpensesNoDataLabel").isVisible());
    }

}

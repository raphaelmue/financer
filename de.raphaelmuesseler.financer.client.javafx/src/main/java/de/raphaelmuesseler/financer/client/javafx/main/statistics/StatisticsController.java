package de.raphaelmuesseler.financer.client.javafx.main.statistics;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.util.collections.Tree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {
    public JFXDatePicker variableExpensesFromDatePicker;
    public JFXDatePicker variableExpensesToDatePicker;
    public JFXDatePicker fixedExpensesFromDatePicker;
    public JFXDatePicker fixedExpensesToDatePicker;
    public PieChart fixedExpensesDistributionChart;
    public PieChart variableExpensesDistributionChart;
    public Label fixedExpensesNoDataLabel;
    public Label variableExpensesNoDataLabel;

    private LocalStorage localStorage = LocalStorageImpl.getInstance();
    private BaseCategory categories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.categories = (BaseCategory) localStorage.readObject("categories");

        this.variableExpensesFromDatePicker.setValue(LocalDate.now().minusMonths(1));
        this.variableExpensesFromDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                this.loadVariableExpensesDistributionChart(newValue, variableExpensesToDatePicker.getValue()));
        this.variableExpensesToDatePicker.setValue(LocalDate.now());
        this.variableExpensesToDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                this.loadVariableExpensesDistributionChart(variableExpensesFromDatePicker.getValue(), newValue));

        this.loadVariableExpensesDistributionChart(this.variableExpensesFromDatePicker.getValue(), this.variableExpensesToDatePicker.getValue());

        this.fixedExpensesFromDatePicker.setValue(LocalDate.now().minusMonths(1));
        this.fixedExpensesFromDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                this.loadFixedExpensesDistributionChart(newValue, variableExpensesToDatePicker.getValue()));
        this.fixedExpensesToDatePicker.setValue(LocalDate.now());
        this.fixedExpensesToDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                this.loadFixedExpensesDistributionChart(variableExpensesFromDatePicker.getValue(), newValue));

        this.loadFixedExpensesDistributionChart(this.variableExpensesFromDatePicker.getValue(), this.variableExpensesToDatePicker.getValue());
    }

    private void loadVariableExpensesDistributionChart(LocalDate startDate, LocalDate endDate) {
        ObservableList<PieChart.Data> variableExpensesData = FXCollections.observableArrayList();
        for (Tree<Category> categoryTree : this.categories.getCategoryTreeByCategoryClass(
                BaseCategory.CategoryClass.VARIABLE_EXPENSES).getChildren()) {
            double amount = ((CategoryTree) categoryTree).getAmount(startDate, endDate);
            if (amount != 0) {
                variableExpensesData.add(new PieChart.Data(categoryTree.getValue().getName(), Math.abs(amount)));
            }
        }

        if (variableExpensesData.size() > 0) {
            this.variableExpensesDistributionChart.setManaged(true);
            this.variableExpensesNoDataLabel.setManaged(false);
            this.variableExpensesDistributionChart.setData(variableExpensesData);
        } else {
            this.variableExpensesDistributionChart.setManaged(false);
            this.variableExpensesNoDataLabel.setManaged(true);
        }
    }

    private void loadFixedExpensesDistributionChart(LocalDate startDate, LocalDate endDate) {
        ObservableList<PieChart.Data> variableExpensesData = FXCollections.observableArrayList();
        for (Tree<Category> categoryTree : this.categories.getCategoryTreeByCategoryClass(
                BaseCategory.CategoryClass.FIXED_EXPENSES).getChildren()) {
            double amount = ((CategoryTree) categoryTree).getAmount(startDate, endDate);
            if (amount != 0) {
                variableExpensesData.add(new PieChart.Data(categoryTree.getValue().getName(), Math.abs(amount)));
            }
        }

        if (variableExpensesData.size() > 0) {
            this.fixedExpensesDistributionChart.setManaged(true);
            this.fixedExpensesNoDataLabel.setManaged(false);
            this.fixedExpensesDistributionChart.setData(variableExpensesData);
        } else {
            this.fixedExpensesDistributionChart.setManaged(false);
            this.fixedExpensesNoDataLabel.setManaged(true);
        }
    }
}

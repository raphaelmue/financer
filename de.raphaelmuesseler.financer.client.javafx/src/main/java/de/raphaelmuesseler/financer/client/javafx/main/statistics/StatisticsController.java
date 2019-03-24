package de.raphaelmuesseler.financer.client.javafx.main.statistics;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerController;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.util.collections.Tree;
import de.raphaelmuesseler.financer.util.date.DateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {
    public JFXDatePicker variableExpensesFromDatePicker;
    public JFXDatePicker variableExpensesToDatePicker;
    public PieChart fixedExpensesDistributionChart;
    public Label fixedExpensesNoDataLabel;

    public JFXDatePicker fixedExpensesFromDatePicker;
    public JFXDatePicker fixedExpensesToDatePicker;
    public PieChart variableExpensesDistributionChart;
    public Label variableExpensesNoDataLabel;

    public JFXDatePicker progressFromDatePicker;
    public JFXDatePicker progressToDatePicker;
    public ComboBox<CategoryTree> progressCategoryComboBox;
    public LineChart<String, Number> progressLineChart;

    private LocalStorage localStorage = LocalStorageImpl.getInstance();
    private BaseCategory categories;
    private Formatter formatter = new JavaFXFormatter(localStorage);

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

        this.progressFromDatePicker.setValue(LocalDate.now().minusMonths(6));
        this.progressFromDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.loadProgressChart(progressCategoryComboBox.getValue(), newValue, progressToDatePicker.getValue());
        });

        this.progressToDatePicker.setValue(LocalDate.now());
        this.progressToDatePicker.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            this.loadProgressChart(progressCategoryComboBox.getValue(), progressFromDatePicker.getValue(), newValue);
        });

        categories.traverse(categoryTree -> progressCategoryComboBox.getItems().add((CategoryTree) categoryTree));
        this.progressCategoryComboBox.getItems().sort((o1, o2)
                -> String.CASE_INSENSITIVE_ORDER.compare(o1.getValue().getPrefix(), o2.getValue().getPrefix()));
        this.progressCategoryComboBox.valueProperty().addListener((observableValue, oldValue, newValue)
                -> this.loadProgressChart(newValue, progressFromDatePicker.getValue(), progressToDatePicker.getValue()));
//        this.progressCategoryComboBox.setCellFactory(param -> new ListCell<>() {
//            @Override
//            protected void updateItem(CategoryTree item, boolean empty) {
//                super.updateItem(item, empty);
//                if (!empty) {
//                    setText(formatter.formatCategoryName(item.getValue()));
//                } else {
//                    setText(null);
//                }
//            }
//        });
        this.progressCategoryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategoryTree categoryTree) {
                return categoryTree != null ? formatter.formatCategoryName(categoryTree) : "";
            }

            @Override
            public CategoryTree fromString(String s) {
                return new CategoryTree(null, new Category(-1, s, -1, -1));
            }
        });
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
            this.variableExpensesDistributionChart.setVisible(true);
            this.variableExpensesNoDataLabel.setManaged(false);
            this.variableExpensesNoDataLabel.setVisible(false);
            this.variableExpensesDistributionChart.setData(variableExpensesData);
        } else {
            this.variableExpensesDistributionChart.setManaged(false);
            this.variableExpensesDistributionChart.setVisible(false);
            this.variableExpensesNoDataLabel.setManaged(true);
            this.variableExpensesNoDataLabel.setVisible(true);
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
            this.fixedExpensesDistributionChart.setVisible(true);
            this.fixedExpensesNoDataLabel.setManaged(false);
            this.fixedExpensesNoDataLabel.setVisible(false);
            this.fixedExpensesDistributionChart.setData(variableExpensesData);
        } else {
            this.fixedExpensesDistributionChart.setManaged(false);
            this.fixedExpensesDistributionChart.setVisible(false);
            this.fixedExpensesNoDataLabel.setManaged(true);
            this.fixedExpensesNoDataLabel.setVisible(true);
        }
    }

    private void loadProgressChart(CategoryTree categoryTree, LocalDate startDate, LocalDate endDate) {
        if (this.progressCategoryComboBox.getValue() != null) {
            FinancerController.getInstance().showLoadingBox();
            this.progressLineChart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(formatter.formatCategoryName(categoryTree));

            for (int i = DateUtil.getMonthDifference(startDate, endDate); i > 0; i--) {
                series.getData().add(new XYChart.Data<>(formatter.formatDate(endDate.minusMonths(i)),
                        Math.abs(categoryTree.getAmount(endDate.minusMonths(i)))));
            }

            this.progressLineChart.getData().add(series);
            FinancerController.getInstance().hideLoadingBox();
        }
    }
}

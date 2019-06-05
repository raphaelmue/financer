package de.raphaelmuesseler.financer.client.javafx.main.statistics;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerController;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import de.raphaelmuesseler.financer.util.date.DateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    @FXML
    public JFXDatePicker variableExpensesFromDatePicker;
    @FXML
    public JFXDatePicker variableExpensesToDatePicker;
    @FXML
    public PieChart fixedExpensesDistributionChart;
    @FXML
    public Label fixedExpensesNoDataLabel;

    @FXML
    public JFXDatePicker fixedExpensesFromDatePicker;
    @FXML
    public JFXDatePicker fixedExpensesToDatePicker;
    @FXML
    public PieChart variableExpensesDistributionChart;
    @FXML
    public Label variableExpensesNoDataLabel;

    @FXML
    public JFXDatePicker progressFromDatePicker;
    @FXML
    public JFXDatePicker progressToDatePicker;
    @FXML
    public LineChart<String, Number> progressLineChart;
    @FXML
    public VBox categoriesContainer;
    @FXML
    public JFXButton addCategoryBtn;
    @FXML
    public ComboBox<CategoryTree> progressChartDefaultCategoryComboBox;

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
            this.progressLineChart.getData().clear();
            for (Node node : categoriesContainer.getChildren()) {
                //noinspection unchecked
                this.loadProgressChartData(((ComboBox<CategoryTree>) ((HBox) node).getChildren().get(0)).getValue(), newValue, progressToDatePicker.getValue());
            }
        });

        this.progressToDatePicker.setValue(LocalDate.now());
        this.progressToDatePicker.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            this.progressLineChart.getData().clear();
            for (Node node : categoriesContainer.getChildren()) {
                //noinspection unchecked
                this.loadProgressChartData(((ComboBox<CategoryTree>) ((HBox) node).getChildren().get(0)).getValue(), progressFromDatePicker.getValue(), newValue);
            }
        });

        this.initializeCategoryComboBox(this.progressChartDefaultCategoryComboBox);
        this.progressChartDefaultCategoryComboBox.getSelectionModel().select(0);

        this.addCategoryBtn.setOnAction(event -> {
            addCategoryBtn.setDisable(true);
            categoriesContainer.getChildren().add(initializeCategoryComboBoxContainer());
        });
    }

    private HBox initializeCategoryComboBoxContainer() {
        final HBox container = new HBox();
        container.setSpacing(10);

        final ComboBox<CategoryTree> categoryTreeComboBox = new ComboBox<>();
        this.initializeCategoryComboBox(categoryTreeComboBox);

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        final JFXButton deleteCategoryBtn = new JFXButton(I18N.get("delete"));
        deleteCategoryBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH));
        deleteCategoryBtn.setOnAction(event -> {
            progressLineChart.getData().removeIf(stringNumberSeries ->
                    stringNumberSeries.getName().equals(formatter.formatCategoryName(categoryTreeComboBox.getValue())));
            addCategoryBtn.setDisable(false);
            categoriesContainer.getChildren().remove(container);
        });

        container.getChildren().add(categoryTreeComboBox);
        container.getChildren().add(deleteCategoryBtn);
        return container;
    }

    private void initializeCategoryComboBox(ComboBox<CategoryTree> categoryTreeComboBox) {
        categoryTreeComboBox.getItems().add(categories);
        categories.traverse(categoryTree -> categoryTreeComboBox.getItems().add((CategoryTree) categoryTree));
        categoryTreeComboBox.getItems().sort((o1, o2)
                -> String.CASE_INSENSITIVE_ORDER.compare(o1.getValue().getPrefix(), o2.getValue().getPrefix()));
        categoryTreeComboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != null) {
                progressLineChart.getData().removeIf(stringNumberSeries ->
                        stringNumberSeries.getName().equals(formatter.formatCategoryName(oldValue)));
            }
            if (newValue != null) {
                this.addCategoryBtn.setDisable(false);
                this.loadProgressChartData(newValue, progressFromDatePicker.getValue(), progressToDatePicker.getValue());
            }
        });
        categoryTreeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategoryTree categoryTree) {
                return categoryTree != null ? formatter.formatCategoryName(categoryTree) : "";
            }

            @Override
            public CategoryTree fromString(String string) {
                return new CategoryTreeImpl(null, new Category(string));
            }
        });
    }

    private void loadProgressChartData(CategoryTree categoryTree, LocalDate startDate, LocalDate endDate) {
        FinancerController.getInstance().showLoadingBox();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(formatter.formatCategoryName(categoryTree));

        for (int i = DateUtil.getMonthDifference(startDate, endDate); i >= 0; i--) {
            series.getData().add(new XYChart.Data<>(formatter.formatDate(endDate.minusMonths(i)),
                    Math.abs(categoryTree.getAmount(endDate.minusMonths(i)))));
        }

        this.progressLineChart.getData().add(series);
        FinancerController.getInstance().hideLoadingBox();
    }

    private void loadVariableExpensesDistributionChart(LocalDate startDate, LocalDate endDate) {
        ObservableList<PieChart.Data> variableExpensesData = FXCollections.observableArrayList();
        for (CategoryTree categoryTree : this.categories.getCategoryTreeByCategoryClass(
                BaseCategory.CategoryClass.VARIABLE_EXPENSES).getChildren()) {
            double amount = categoryTree.getAmount(startDate, endDate);
            if (amount != 0) {
                variableExpensesData.add(new PieChart.Data(categoryTree.getValue().getName(), Math.abs(amount)));
            }
        }

        if (!variableExpensesData.isEmpty()) {
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
        for (CategoryTree categoryTree : this.categories.getCategoryTreeByCategoryClass(
                BaseCategory.CategoryClass.FIXED_EXPENSES).getChildren()) {
            double amount = categoryTree.getAmount(startDate, endDate);
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
}

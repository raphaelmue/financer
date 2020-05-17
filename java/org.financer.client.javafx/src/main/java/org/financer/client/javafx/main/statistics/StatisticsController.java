package org.financer.client.javafx.main.statistics;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import org.financer.client.domain.api.RestApi;
import org.financer.client.domain.api.RestApiImpl;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.category.CategoryRoot;
import org.financer.client.format.Formatter;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.DatePicker;
import org.financer.client.javafx.components.charts.DonutChart;
import org.financer.client.javafx.components.charts.SmoothedChart;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.client.javafx.main.FinancerController;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    @FXML
    public DatePicker variableExpensesFromDatePicker;
    @FXML
    public DatePicker variableExpensesToDatePicker;
    @FXML
    public DonutChart fixedExpensesDistributionChart;
    @FXML
    public Label fixedExpensesNoDataLabel;

    @FXML
    public DatePicker fixedExpensesFromDatePicker;
    @FXML
    public DatePicker fixedExpensesToDatePicker;
    @FXML
    public DonutChart variableExpensesDistributionChart;
    @FXML
    public Label variableExpensesNoDataLabel;

    @FXML
    public DatePicker progressFromDatePicker;
    @FXML
    public DatePicker progressToDatePicker;
    @FXML
    public VBox categoriesContainer;
    @FXML
    public JFXButton addCategoryBtn;
    @FXML
    public JFXComboBox<Category> progressChartDefaultCategoryComboBox;
    @FXML
    public SmoothedChart<String, Number> progressLineChart;

    private final RestApi restApi = new RestApiImpl();
    private LocalStorage localStorage = LocalStorageImpl.getInstance();
    private CategoryRoot categoryRoot;
    private Formatter formatter = new JavaFXFormatter(localStorage);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FinancerController.setInitializationThread(new Thread(() -> {
            FinancerController.getInstance().showLoadingBox();
            this.categoryRoot = localStorage.readObject("categories");

            this.initializeProgressChart();
            this.initializeVariableExpensesDistributionChart();
            this.initializeFixedExpensesDistributionChart();

            FinancerController.getInstance().hideLoadingBox();
        }));
        FinancerController.getInitializationThread().start();
    }

    private void initializeProgressChart() {
        this.progressLineChart.setChartType(SmoothedChart.ChartType.AREA);
        this.progressLineChart.setId("progressLineChart");
        this.progressLineChart.setPrefWidth(500);

        Platform.runLater(() -> {
            this.progressFromDatePicker.setValue(LocalDate.now().minusMonths(6));
            this.progressFromDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> new Thread(() -> {
                FinancerController.getInstance().showLoadingBox();
                Platform.runLater(() -> this.progressLineChart.getData().clear());
                for (Node node : categoriesContainer.getChildren()) {
                    //noinspection unchecked
                    this.loadProgressChartData(((ComboBox<Category>) ((HBox) node).getChildren().get(0)).getValue(), newValue, progressToDatePicker.getValue());
                }
                FinancerController.getInstance().hideLoadingBox();
            }).start());
        });

        Platform.runLater(() -> {
            this.progressToDatePicker.setValue(LocalDate.now());
            this.progressToDatePicker.valueProperty().addListener((observableValue, oldValue, newValue) -> new Thread(() -> {
                FinancerController.getInstance().showLoadingBox();
                Platform.runLater(() -> this.progressLineChart.getData().clear());
                for (Node node : categoriesContainer.getChildren()) {
                    //noinspection unchecked
                    this.loadProgressChartData(((ComboBox<Category>) ((HBox) node).getChildren().get(0)).getValue(), progressFromDatePicker.getValue(), newValue);
                }
                FinancerController.getInstance().hideLoadingBox();
            }).start());
        });

        this.initializeCategoryComboBox(this.progressChartDefaultCategoryComboBox);

        this.addCategoryBtn.setOnAction(event -> {
            addCategoryBtn.setDisable(true);
            categoriesContainer.getChildren().add(initializeCategoryComboBoxContainer());
        });

        Platform.runLater(() -> this.progressChartDefaultCategoryComboBox.getSelectionModel().select(0));
    }

    private HBox initializeCategoryComboBoxContainer() {
        final HBox container = new HBox();
        container.setSpacing(10);

        final JFXComboBox<Category> categoryComboBox = new JFXComboBox<>();
        this.initializeCategoryComboBox(categoryComboBox);

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        final JFXButton deleteCategoryBtn = new JFXButton(I18N.get("delete"));
        deleteCategoryBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH));
        deleteCategoryBtn.setOnAction(event -> {
            if (categoryComboBox.getValue() != null) {
                progressLineChart.getData().removeIf(stringNumberSeries ->
                        stringNumberSeries.getName().equals(formatter.format(categoryComboBox.getValue())));
            }
            addCategoryBtn.setDisable(false);
            categoriesContainer.getChildren().remove(container);
        });

        container.getChildren().add(categoryComboBox);
        container.getChildren().add(deleteCategoryBtn);
        return container;
    }

    private void initializeCategoryComboBox(ComboBox<Category> categoryComboBox) {
        categoryRoot.traverse(category -> categoryComboBox.getItems().add((Category) category));
        categoryComboBox.getItems().sort((o1, o2)
                -> String.CASE_INSENSITIVE_ORDER.compare(o1.getPrefix(), o2.getPrefix()));
        categoryComboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> new Thread(() -> {
            FinancerController.getInstance().showLoadingBox();
            if (oldValue != null) {
                Platform.runLater(() -> progressLineChart.getData().removeIf(stringNumberSeries ->
                        stringNumberSeries.getName().equals(formatter.format(oldValue))));
            }
            if (newValue != null) {
                this.addCategoryBtn.setDisable(false);
                this.loadProgressChartData(newValue, progressFromDatePicker.getValue(), progressToDatePicker.getValue());
            }
            FinancerController.getInstance().hideLoadingBox();
        }).start());
        categoryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category != null ? formatter.format(category) : "";
            }

            @Override
            public Category fromString(String string) {
                return new Category().setName(string);
            }
        });
    }

    private void loadProgressChartData(Category category, LocalDate startDate, LocalDate endDate) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(formatter.format(category));

//        for (int i = DateUtil.getMonthDifference(startDate, endDate); i >= 0; i--) {
//            XYChart.Data<String, Number> dataSet = new XYChart.Data<>(formatter.format(endDate.minusMonths(i)),
//                    category.getAmount(new ValueDate(endDate.minusMonths(i))));
//            Platform.runLater(() -> Tooltip.install(dataSet.getNode(),
//                    new Tooltip(I18N.get("category") + ": \t" + formatter.format(category) + "\n" +
//                            I18N.get("valueDate") + ": \t" + dataSet.getXValue() + "\n" +
//                            I18N.get("amount") + ": \t" + formatter.format((Double) dataSet.getYValue()))));
//            series.getData().add(dataSet);
//        }

        Platform.runLater(() -> this.progressLineChart.getData().add(series));
    }

    private void initializeVariableExpensesDistributionChart() {
        this.variableExpensesFromDatePicker.setValue(LocalDate.now().minusMonths(1));
        this.variableExpensesFromDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                this.loadVariableExpensesDistributionChart(newValue, variableExpensesToDatePicker.getValue()));
        this.variableExpensesToDatePicker.setValue(LocalDate.now());
        this.variableExpensesToDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                this.loadVariableExpensesDistributionChart(variableExpensesFromDatePicker.getValue(), newValue));

        this.loadVariableExpensesDistributionChart(this.variableExpensesFromDatePicker.getValue(), this.variableExpensesToDatePicker.getValue());
    }

    private void loadVariableExpensesDistributionChart(LocalDate startDate, LocalDate endDate) {
        ObservableList<PieChart.Data> variableExpensesData = FXCollections.observableArrayList();
        for (Category category : this.categoryRoot.getCategoriesByClass(
                CategoryClass.Values.VARIABLE_EXPENSES)) {
            Amount amount = category.getAmount(new TimeRange(startDate, endDate));
            if (amount.isNotNull()) {
                variableExpensesData.add(new PieChart.Data(category.getName(), Math.abs(amount.getAmount())));
            }
        }

        if (!variableExpensesData.isEmpty()) {
            this.variableExpensesDistributionChart.setManaged(true);
            this.variableExpensesDistributionChart.setVisible(true);
            this.variableExpensesNoDataLabel.setManaged(false);
            this.variableExpensesNoDataLabel.setVisible(false);
            Platform.runLater(() -> this.variableExpensesDistributionChart.setData(variableExpensesData));
        } else {
            this.variableExpensesDistributionChart.setManaged(false);
            this.variableExpensesDistributionChart.setVisible(false);
            this.variableExpensesNoDataLabel.setManaged(true);
            this.variableExpensesNoDataLabel.setVisible(true);
        }
    }

    private void initializeFixedExpensesDistributionChart() {
        this.fixedExpensesFromDatePicker.setValue(LocalDate.now().minusMonths(1));
        this.fixedExpensesFromDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                this.loadFixedExpensesDistributionChart(newValue, variableExpensesToDatePicker.getValue()));
        this.fixedExpensesToDatePicker.setValue(LocalDate.now());
        this.fixedExpensesToDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                this.loadFixedExpensesDistributionChart(variableExpensesFromDatePicker.getValue(), newValue));

        this.loadFixedExpensesDistributionChart(this.variableExpensesFromDatePicker.getValue(), this.variableExpensesToDatePicker.getValue());
    }

    private void loadFixedExpensesDistributionChart(LocalDate startDate, LocalDate endDate) {
        ObservableList<PieChart.Data> variableExpensesData = FXCollections.observableArrayList();
        for (Category category : this.categoryRoot.getCategoriesByClass(
                CategoryClass.Values.FIXED_EXPENSES)) {
            Amount amount = category.getAmount(new TimeRange(startDate, endDate));
            if (amount.isNotNull()) {
                variableExpensesData.add(new PieChart.Data(category.getName(), Math.abs(amount.getAmount())));
            }
        }

        if (!variableExpensesData.isEmpty()) {
            this.fixedExpensesDistributionChart.setManaged(true);
            this.fixedExpensesDistributionChart.setVisible(true);
            this.fixedExpensesNoDataLabel.setManaged(false);
            this.fixedExpensesNoDataLabel.setVisible(false);
            Platform.runLater(() -> this.fixedExpensesDistributionChart.setData(variableExpensesData));
        } else {
            this.fixedExpensesDistributionChart.setManaged(false);
            this.fixedExpensesDistributionChart.setVisible(false);
            this.fixedExpensesNoDataLabel.setManaged(true);
            this.fixedExpensesNoDataLabel.setVisible(true);
        }
    }
}

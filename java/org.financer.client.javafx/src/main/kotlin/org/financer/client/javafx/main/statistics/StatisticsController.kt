package org.financer.client.javafx.main.statistics

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXComboBox
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.chart.PieChart
import javafx.scene.chart.XYChart
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.financer.client.domain.api.RestApi
import org.financer.client.domain.api.RestApiImpl
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.category.CategoryRoot
import org.financer.client.format.Formatter
import org.financer.client.format.I18N.get
import org.financer.client.javafx.components.DatePicker
import org.financer.client.javafx.components.charts.DonutChart
import org.financer.client.javafx.components.charts.SmoothedChart
import org.financer.client.javafx.format.JavaFXFormatter
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.client.javafx.main.FinancerController
import org.financer.shared.domain.model.value.objects.CategoryClass
import org.financer.shared.domain.model.value.objects.TimeRange
import org.financer.util.collections.Tree
import java.net.URL
import java.time.LocalDate
import java.util.*

class StatisticsController : Initializable {
    @FXML
    var variableExpensesFromDatePicker: DatePicker? = null

    @FXML
    var variableExpensesToDatePicker: DatePicker? = null

    @FXML
    var fixedExpensesDistributionChart: DonutChart? = null

    @FXML
    var fixedExpensesNoDataLabel: Label? = null

    @FXML
    var fixedExpensesFromDatePicker: DatePicker? = null

    @FXML
    var fixedExpensesToDatePicker: DatePicker? = null

    @FXML
    var variableExpensesDistributionChart: DonutChart? = null

    @FXML
    var variableExpensesNoDataLabel: Label? = null

    @FXML
    var progressFromDatePicker: DatePicker? = null

    @FXML
    var progressToDatePicker: DatePicker? = null

    @FXML
    var categoriesContainer: VBox? = null

    @FXML
    var addCategoryBtn: JFXButton? = null

    @FXML
    var progressChartDefaultCategoryComboBox: JFXComboBox<Category>? = null

    @FXML
    var progressLineChart: SmoothedChart<String, Number>? = null
    private val restApi: RestApi = RestApiImpl()
    private val localStorage = LocalStorageImpl.getInstance()
    private lateinit var categoryRoot: CategoryRoot
    private val formatter: Formatter = JavaFXFormatter(localStorage)
    override fun initialize(url: URL, resourceBundle: ResourceBundle) {
        CoroutineScope(Dispatchers.Default).launch {
            FinancerController.instance!!.showLoadingBox()
            categoryRoot = localStorage.readObject("categories") ?: throw IllegalStateException("")
            initializeProgressChart()
            initializeVariableExpensesDistributionChart()
            initializeFixedExpensesDistributionChart()
            FinancerController.instance!!.hideLoadingBox()
        }
    }

    private fun initializeProgressChart() {
        progressLineChart!!.setChartType(SmoothedChart.ChartType.AREA)
        progressLineChart!!.id = "progressLineChart"
        progressLineChart!!.prefWidth = 500.0
        Platform.runLater {
            progressFromDatePicker!!.value = LocalDate.now().minusMonths(6)
            progressFromDatePicker!!.valueProperty().addListener { _: ObservableValue<out LocalDate>?, _: LocalDate?, newValue: LocalDate ->
                Thread(Runnable {
                    FinancerController.instance!!.showLoadingBox()
                    Platform.runLater { progressLineChart!!.data.clear() }
                    for (node in categoriesContainer!!.children) {
                        loadProgressChartData(((node as HBox).children[0] as ComboBox<Category>).value, newValue, progressToDatePicker!!.value)
                    }
                    FinancerController.instance!!.hideLoadingBox()
                }).start()
            }
        }
        Platform.runLater {
            progressToDatePicker!!.value = LocalDate.now()
            progressToDatePicker!!.valueProperty().addListener { _: ObservableValue<out LocalDate>?, _: LocalDate?, newValue: LocalDate ->
                Thread(Runnable {
                    FinancerController.instance!!.showLoadingBox()
                    Platform.runLater { progressLineChart!!.data.clear() }
                    for (node in categoriesContainer!!.children) {
                        loadProgressChartData(((node as HBox).children[0] as ComboBox<Category>).value, progressFromDatePicker!!.value, newValue)
                    }
                    FinancerController.instance!!.hideLoadingBox()
                }).start()
            }
        }
        initializeCategoryComboBox(progressChartDefaultCategoryComboBox)
        addCategoryBtn!!.onAction = EventHandler {
            addCategoryBtn!!.isDisable = true
            categoriesContainer!!.children.add(initializeCategoryComboBoxContainer())
        }
        Platform.runLater { progressChartDefaultCategoryComboBox!!.selectionModel.select(0) }
    }

    private fun initializeCategoryComboBoxContainer(): HBox {
        val container = HBox()
        container.spacing = 10.0
        val categoryComboBox = JFXComboBox<Category>()
        initializeCategoryComboBox(categoryComboBox)
        val fontAwesome = GlyphFontRegistry.font("FontAwesome")
        val deleteCategoryBtn = JFXButton(get("delete"))
        deleteCategoryBtn.graphic = fontAwesome.create(FontAwesome.Glyph.TRASH)
        deleteCategoryBtn.onAction = EventHandler {
            if (categoryComboBox.value != null) {
                progressLineChart!!.data.removeIf { stringNumberSeries: XYChart.Series<String, Number> -> stringNumberSeries.name == formatter.format(categoryComboBox.value!!) }
            }
            addCategoryBtn!!.isDisable = false
            categoriesContainer!!.children.remove(container)
        }
        container.children.add(categoryComboBox)
        container.children.add(deleteCategoryBtn)
        return container
    }

    private fun initializeCategoryComboBox(categoryComboBox: ComboBox<Category>?) {
        categoryRoot.traverse { category: Tree? -> categoryComboBox!!.items.add(category as Category?) }
        categoryComboBox!!.items.sortWith(Comparator { o1: Category, o2: Category -> java.lang.String.CASE_INSENSITIVE_ORDER.compare(o1.prefix, o2.prefix) })
        categoryComboBox.valueProperty().addListener { _: ObservableValue<out Category?>?, oldValue: Category?, newValue: Category? ->
            Thread(Runnable {
                FinancerController.instance!!.showLoadingBox()
                if (oldValue != null) {
                    Platform.runLater { progressLineChart!!.data.removeIf { stringNumberSeries: XYChart.Series<String, Number> -> stringNumberSeries.name == formatter.format(oldValue) } }
                }
                if (newValue != null) {
                    addCategoryBtn!!.isDisable = false
                    loadProgressChartData(newValue, progressFromDatePicker!!.value, progressToDatePicker!!.value)
                }
                FinancerController.instance!!.hideLoadingBox()
            }).start()
        }
        categoryComboBox.setConverter(object : StringConverter<Category>() {
            override fun toString(category: Category): String? {
                return formatter.format(category)
            }

            override fun fromString(string: String): Category {
                return Category().setName(string)
            }
        })
    }

    private fun loadProgressChartData(category: Category?, startDate: LocalDate, endDate: LocalDate) {
        val series = XYChart.Series<String, Number>()
        series.name = formatter.format(category!!)

//        for (int i = DateUtil.getMonthDifference(startDate, endDate); i >= 0; i--) {
//            XYChart.Data<String, Number> dataSet = new XYChart.Data<>(formatter.format(endDate.minusMonths(i)),
//                    category.getAmount(new ValueDate(endDate.minusMonths(i))));
//            Platform.runLater(() -> Tooltip.install(dataSet.getNode(),
//                    new Tooltip(I18N.get("category") + ": \t" + formatter.format(category) + "\n" +
//                            I18N.get("valueDate") + ": \t" + dataSet.getXValue() + "\n" +
//                            I18N.get("amount") + ": \t" + formatter.format((Double) dataSet.getYValue()))));
//            series.getData().add(dataSet);
//        }
        Platform.runLater { progressLineChart!!.data.add(series) }
    }

    private fun initializeVariableExpensesDistributionChart() {
        variableExpensesFromDatePicker!!.value = LocalDate.now().minusMonths(1)
        variableExpensesFromDatePicker!!.valueProperty().addListener { observable: ObservableValue<out LocalDate>?, oldValue: LocalDate?, newValue: LocalDate -> loadVariableExpensesDistributionChart(newValue, variableExpensesToDatePicker!!.value) }
        variableExpensesToDatePicker!!.value = LocalDate.now()
        variableExpensesToDatePicker!!.valueProperty().addListener { observable: ObservableValue<out LocalDate>?, oldValue: LocalDate?, newValue: LocalDate -> loadVariableExpensesDistributionChart(variableExpensesFromDatePicker!!.value, newValue) }
        loadVariableExpensesDistributionChart(variableExpensesFromDatePicker!!.value, variableExpensesToDatePicker!!.value)
    }

    private fun loadVariableExpensesDistributionChart(startDate: LocalDate, endDate: LocalDate) {
        val variableExpensesData = FXCollections.observableArrayList<PieChart.Data>()
        for (category in categoryRoot.getCategoriesByClass(
                CategoryClass.Values.VARIABLE_EXPENSES)) {
            val amount = category.getAmount(TimeRange(startDate, endDate))
            if (amount.isNotNull) {
                variableExpensesData.add(PieChart.Data(category.name, Math.abs(amount.amount)))
            }
        }
        if (!variableExpensesData.isEmpty()) {
            variableExpensesDistributionChart!!.isManaged = true
            variableExpensesDistributionChart!!.isVisible = true
            variableExpensesNoDataLabel!!.isManaged = false
            variableExpensesNoDataLabel!!.isVisible = false
            Platform.runLater { variableExpensesDistributionChart!!.data = variableExpensesData }
        } else {
            variableExpensesDistributionChart!!.isManaged = false
            variableExpensesDistributionChart!!.isVisible = false
            variableExpensesNoDataLabel!!.isManaged = true
            variableExpensesNoDataLabel!!.isVisible = true
        }
    }

    private fun initializeFixedExpensesDistributionChart() {
        fixedExpensesFromDatePicker!!.value = LocalDate.now().minusMonths(1)
        fixedExpensesFromDatePicker!!.valueProperty().addListener { observable: ObservableValue<out LocalDate>?, oldValue: LocalDate?, newValue: LocalDate -> loadFixedExpensesDistributionChart(newValue, variableExpensesToDatePicker!!.value) }
        fixedExpensesToDatePicker!!.value = LocalDate.now()
        fixedExpensesToDatePicker!!.valueProperty().addListener { observable: ObservableValue<out LocalDate>?, oldValue: LocalDate?, newValue: LocalDate -> loadFixedExpensesDistributionChart(variableExpensesFromDatePicker!!.value, newValue) }
        loadFixedExpensesDistributionChart(variableExpensesFromDatePicker!!.value, variableExpensesToDatePicker!!.value)
    }

    private fun loadFixedExpensesDistributionChart(startDate: LocalDate, endDate: LocalDate) {
        val variableExpensesData = FXCollections.observableArrayList<PieChart.Data>()
        for (category in categoryRoot.getCategoriesByClass(
                CategoryClass.Values.FIXED_EXPENSES)) {
            val amount = category.getAmount(TimeRange(startDate, endDate))
            if (amount.isNotNull) {
                variableExpensesData.add(PieChart.Data(category.name, Math.abs(amount.amount)))
            }
        }
        if (!variableExpensesData.isEmpty()) {
            fixedExpensesDistributionChart!!.isManaged = true
            fixedExpensesDistributionChart!!.isVisible = true
            fixedExpensesNoDataLabel!!.isManaged = false
            fixedExpensesNoDataLabel!!.isVisible = false
            Platform.runLater { fixedExpensesDistributionChart!!.data = variableExpensesData }
        } else {
            fixedExpensesDistributionChart!!.isManaged = false
            fixedExpensesDistributionChart!!.isVisible = false
            fixedExpensesNoDataLabel!!.isManaged = true
            fixedExpensesNoDataLabel!!.isVisible = true
        }
    }
}
package org.financer.client.javafx.main.overview

import com.jfoenix.controls.JFXComboBox
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.HPos
import javafx.scene.chart.PieChart
import javafx.scene.chart.XYChart
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.financer.client.domain.api.RestApi
import org.financer.client.domain.api.RestApiImpl
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.category.CategoryRoot
import org.financer.client.domain.model.transaction.FixedTransaction
import org.financer.client.domain.model.transaction.FixedTransactionAmount
import org.financer.client.domain.model.transaction.Transaction
import org.financer.client.domain.model.transaction.VariableTransaction
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N.get
import org.financer.client.javafx.components.charts.DonutChart
import org.financer.client.javafx.components.charts.SmoothedChart
import org.financer.client.javafx.format.JavaFXFormatter
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.client.javafx.main.FinancerController
import org.financer.client.javafx.main.transactions.TransactionAmountDialog
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.CategoryClass
import org.financer.shared.domain.model.value.objects.TimeRange
import org.financer.shared.domain.model.value.objects.ValueDate
import org.financer.util.collections.Action
import org.financer.util.collections.Tree
import org.financer.util.collections.TreeUtil
import java.net.URL
import java.time.LocalDate
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class OverviewController : Initializable {
    @FXML
    var lastTransactionsGridPane: GridPane? = null

    @FXML
    var balanceGridPane: GridPane? = null

    @FXML
    var upcomingFixedTransactionGridPane: GridPane? = null

    @FXML
    var balanceChangeLabel: Label? = null

    @FXML
    var balanceLabel: Label? = null

    @FXML
    var variableExpensesLabel: Label? = null

    @FXML
    var variableExpensesChangeLabel: Label? = null

    @FXML
    var numberOfTransactionsChangeLabel: Label? = null

    @FXML
    var numberOfTransactionsLabel: Label? = null

    @FXML
    var balanceChartMonthComboBox: JFXComboBox<String>? = null

    @FXML
    var variableExpensesDistributionMonthComboBox: JFXComboBox<String>? = null

    @FXML
    var variableExpensesDistributionPieChart: DonutChart? = null

    @FXML
    var balanceChart: SmoothedChart<String?, Number>? = null
    private val restApi: RestApi = RestApiImpl()
    private var balanceAmount = Amount()
    private val localStorage = LocalStorageImpl.getInstance() as LocalStorageImpl
    private val formatter = JavaFXFormatter(localStorage)
    private lateinit var categoryRoot: CategoryRoot
    private var user: User? = null
    override fun initialize(location: URL, resources: ResourceBundle) {
        CoroutineScope(Dispatchers.Default).launch {
            FinancerController.instance!!.showLoadingBox()
            categoryRoot = localStorage.readObject("categories")
                    ?: throw IllegalStateException("There are no categories stored")
            user = localStorage.readObject("user")
            loadDetailedBalance()
            loadLatestTransactions()
            loadUpcomingFixedTransactions()
            loadBalanceWidget()
            loadVariableExpensesWidget()
            loadNumberOfTransactionsWidget()
            initializeBalanceChart()
            initializeDistributionChart()
            FinancerController.instance!!.hideLoadingBox()
        }
    }

    private fun loadBalanceWidget() {
        val balanceRatio = balanceAmount.calcRatio(categoryRoot!!.getAmount(ValueDate(LocalDate.now().minusMonths(1))))
        Platform.runLater {
            balanceLabel!!.text = formatter.format(balanceAmount)
            formatter.format(balanceChangeLabel!!, balanceRatio)
            if (!java.lang.Double.isNaN(balanceRatio.amount) && java.lang.Double.isFinite(balanceRatio.amount)) {
                balanceChangeLabel!!.text = (if (balanceRatio.isNegative) "" else "+") + balanceChangeLabel!!.text
            }
        }
    }

    private fun loadVariableExpensesWidget() {
        val variableExpensesAmount = categoryRoot!!.getAmount(CategoryClass.Values.VARIABLE_EXPENSES, ValueDate())
        val variableExpensesRatio = variableExpensesAmount.calcRatio(categoryRoot!!.getAmount(CategoryClass.Values.VARIABLE_EXPENSES, ValueDate(LocalDate.now().minusMonths(1))))
        Platform.runLater {
            variableExpensesLabel!!.text = formatter.format(variableExpensesAmount)
            formatter.formatChangeLabel(variableExpensesChangeLabel!!, variableExpensesRatio.amount)
            if (!java.lang.Double.isNaN(variableExpensesRatio.amount) && java.lang.Double.isFinite(variableExpensesRatio.amount)) variableExpensesChangeLabel!!.text = (if (variableExpensesRatio.isNegative) "" else "+") + variableExpensesChangeLabel!!.text
        }
    }

    private fun loadNumberOfTransactionsWidget() {
        val numberOfTransactions = AtomicInteger()
        TreeUtil.traverse(categoryRoot!!.getCategoriesByClass(CategoryClass.Values.VARIABLE_EXPENSES)
        ) { `object`: Tree ->
            numberOfTransactions.addAndGet((`object` as Category).transactions.stream()
                    .filter { transaction: Transaction -> (transaction as VariableTransaction).valueDate!!.isInSameMonth(ValueDate()) }
                    .count().toInt())
        }
        TreeUtil.traverse(categoryRoot!!.getCategoriesByClass(CategoryClass.Values.VARIABLE_REVENUE)
        ) { `object`: Tree ->
            numberOfTransactions.addAndGet((`object` as Category).transactions.stream()
                    .filter { transaction: Transaction -> (transaction as VariableTransaction).valueDate!!.isInSameMonth(ValueDate()) }
                    .count().toInt())
        }
        val numberOfTransactionsLastMonth = AtomicInteger()
        TreeUtil.traverse(categoryRoot!!.getCategoriesByClass(CategoryClass.Values.VARIABLE_EXPENSES)
        ) { `object`: Tree ->
            numberOfTransactionsLastMonth.addAndGet((`object` as Category).transactions.stream()
                    .filter { transaction: Transaction -> (transaction as VariableTransaction).valueDate!!.isInSameMonth(ValueDate(LocalDate.now().minusMonths(1))) }
                    .count().toInt())
        }
        TreeUtil.traverse(categoryRoot!!.getCategoriesByClass(CategoryClass.Values.VARIABLE_REVENUE)
        ) { `object`: Tree ->
            numberOfTransactionsLastMonth.addAndGet((`object` as Category).transactions.stream()
                    .filter { transaction: Transaction -> (transaction as VariableTransaction).valueDate!!.isInSameMonth(ValueDate(LocalDate.now().minusMonths(1))) }
                    .count().toInt())
        }
        val numberOfTransactionsRatio = numberOfTransactions.get().toDouble() / numberOfTransactionsLastMonth.get().toDouble() * 100 - 100
        Platform.runLater {
            numberOfTransactionsLabel!!.text = Integer.toString(numberOfTransactions.get())
            formatter.formatChangeLabel(numberOfTransactionsChangeLabel!!, numberOfTransactionsRatio)
            if (!java.lang.Double.isNaN(numberOfTransactionsRatio) && java.lang.Double.isFinite(numberOfTransactionsRatio)) numberOfTransactionsChangeLabel!!.text = ((if (numberOfTransactionsRatio < 0) "" else "+")
                    + numberOfTransactionsChangeLabel!!.text)
        }
    }

    private fun loadLatestTransactions() {
        val transactions: MutableList<VariableTransaction> = ArrayList()
        categoryRoot!!.traverse { categoryTree: Tree ->
            for (transaction in (categoryTree as Category).transactions) {
                if (!transaction.isFixed) {
                    transactions.add(transaction as VariableTransaction)
                }
            }
        }
        transactions.sortWith(Comparator { o1: VariableTransaction, o2: VariableTransaction -> o2.valueDate!!.compareTo(o1.valueDate) })
        lastTransactionsGridPane!!.vgap = 8.0
        if (!transactions.isEmpty()) {
            var counter = 0
            for (transaction in transactions) {
                // LAST TRANSACTIONS
                if (counter >= 7) {
                    break
                }
                val _counter = counter
                Platform.runLater {
                    lastTransactionsGridPane!!.add(Label(transaction.category!!.name),
                            0, _counter)
                }
                val amountLabel = formatter.format(Label(), transaction.amount)
                Platform.runLater { lastTransactionsGridPane!!.add(amountLabel, 1, _counter) }
                GridPane.setHalignment(amountLabel, HPos.RIGHT)
                GridPane.setHgrow(amountLabel, Priority.ALWAYS)
                GridPane.setVgrow(amountLabel, Priority.ALWAYS)
                counter++
            }
        } else {
            Platform.runLater { lastTransactionsGridPane!!.add(Label(get("noRecentTransactions")), 0, 0) }
        }
    }

    private fun loadDetailedBalance() {
        var counter = 0
        for (categoryClass in CategoryClass.getAll()) {
            val _counter = counter
            Platform.runLater { balanceGridPane!!.add(Label(get(categoryClass.categoryClass.getName())), 0, _counter) }
            val amount = categoryRoot!!.getAmount(categoryClass.categoryClass, ValueDate())
            val baseCategoryLabel = formatter.format(Label(), amount)
            balanceAmount = balanceAmount.add(amount)
            GridPane.setHalignment(baseCategoryLabel, HPos.RIGHT)
            GridPane.setHgrow(baseCategoryLabel, Priority.ALWAYS)
            GridPane.setVgrow(baseCategoryLabel, Priority.ALWAYS)
            Platform.runLater { balanceGridPane!!.add(baseCategoryLabel, 1, _counter) }
            counter++
        }
        val balanceTextLabel = Label(get("balance"))
        balanceTextLabel.id = "balance-label"
        Platform.runLater { balanceGridPane!!.add(balanceTextLabel, 0, 4) }
        val balanceLabel = formatter.format(Label(), balanceAmount)
        balanceLabel.id = "balance-amount"
        GridPane.setHalignment(balanceLabel, HPos.RIGHT)
        GridPane.setHgrow(balanceLabel, Priority.ALWAYS)
        GridPane.setVgrow(balanceLabel, Priority.ALWAYS)
        Platform.runLater { balanceGridPane!!.add(balanceLabel, 1, 4) }
    }

    private fun loadUpcomingFixedTransactions() {
        val transactions: MutableList<FixedTransaction> = ArrayList()
        categoryRoot!!.traverse { categoryTree: Tree ->
            for (transaction in (categoryTree as Category).transactions) {
                if (transaction.isFixed && (transaction as FixedTransaction).isActive &&
                        transaction.getAmount(ValueDate()).amount == 0.0) {
                    transactions.add(transaction)
                }
            }
        }
        if (!transactions.isEmpty()) {
            var counter = 0
            for (transaction in transactions) {
                // LAST TRANSACTIONS
                if (counter >= 5) {
                    break
                }
                val _counter = counter
                if (transaction.isVariable) {
                    val link = Hyperlink(transaction.category!!.name)
                    link.onAction = EventHandler { event: ActionEvent? -> addTransactionAmount(transaction) }
                    Platform.runLater { upcomingFixedTransactionGridPane!!.add(link, 0, _counter) }
                } else {
                    Platform.runLater {
                        upcomingFixedTransactionGridPane!!.add(Label(transaction.category!!.name),
                                0, _counter)
                    }
                }
                val dayLabel = Label(formatter.format(LocalDate.now().withDayOfMonth(transaction.day)))
                Platform.runLater { upcomingFixedTransactionGridPane!!.add(dayLabel, 1, _counter) }
                GridPane.setHalignment(dayLabel, HPos.RIGHT)
                GridPane.setHgrow(dayLabel, Priority.ALWAYS)
                GridPane.setVgrow(dayLabel, Priority.ALWAYS)
                counter++
            }
        } else {
            Platform.runLater { upcomingFixedTransactionGridPane!!.add(Label(get("noUpcomingTransactions")), 0, 0) }
        }
    }

    private fun addTransactionAmount(transaction: FixedTransaction) {
        val transactionAmount = FixedTransactionAmount()
                .setId(0)
                .setAmount(Amount())
                .setValueDate(ValueDate())
        transactionAmount.setFixedTransaction(transaction)
        val dialog = TransactionAmountDialog(transactionAmount, ArrayList(transaction.getTransactionAmounts()))
        dialog.setOnConfirm(Action {
            CoroutineScope(Dispatchers.IO).launch {
                val updatedTransactionAmount = restApi.createTransactionAmount(transaction.id, transactionAmount)
                if (updatedTransactionAmount != null) {
                    transaction.getTransactionAmounts().add(updatedTransactionAmount)
                    localStorage.writeObject("categories", categoryRoot)
                    Platform.runLater { upcomingFixedTransactionGridPane!!.children.clear() }
                    loadUpcomingFixedTransactions()
                }
            }
        })
    }

    private fun initializeBalanceChart() {
        balanceChart!!.setChartType(SmoothedChart.ChartType.AREA)
        balanceChart!!.animated = false
        balanceChartMonthComboBox!!.items.add(get("lastMonths", 3))
        balanceChartMonthComboBox!!.items.add(get("lastMonths", 6))
        balanceChartMonthComboBox!!.items.add(get("lastMonths", 12))
        balanceChartMonthComboBox!!.valueProperty().addListener { options: ObservableValue<out String>?, oldValue: String?, newValue: String? -> loadBalanceChartData() }
        Platform.runLater { balanceChartMonthComboBox!!.selectionModel.select(0) }
    }

    private fun loadBalanceChartData() {
        balanceChart!!.data.clear()
        val data = XYChart.Series<String?, Number>()
        data.name = get("balance")
        val numberOfMonths = (1.5 * Math.pow(balanceChartMonthComboBox!!.selectionModel.selectedIndex.toDouble(), 2.0) + 1.5 * balanceChartMonthComboBox!!.selectionModel.selectedIndex + 3).toInt()
        for (i in numberOfMonths - 1 downTo 0) {
            val date = LocalDate.now().minusMonths(i.toLong())
            val dataSet = XYChart.Data<String?, Number>(formatter.format(date), categoryRoot!!.getAmount(ValueDate(date)).amount)
            Platform.runLater {
                Tooltip.install(dataSet.node,
                        Tooltip("""
    ${dataSet.xValue.toString()}
    ${get("amount")}: 	${formatter.format(dataSet.yValue as Double)}
    """.trimIndent()))
            }
            data.data.add(dataSet)
        }
        balanceChart!!.data.add(data)
    }

    private fun initializeDistributionChart() {
        variableExpensesDistributionMonthComboBox!!.items.add(get("thisMonth"))
        variableExpensesDistributionMonthComboBox!!.items.add(get("lastMonths", 3))
        variableExpensesDistributionMonthComboBox!!.items.add(get("lastMonths", 6))
        variableExpensesDistributionMonthComboBox!!.items.add(get("lastMonths", 12))
        variableExpensesDistributionMonthComboBox!!.valueProperty().addListener { options: ObservableValue<out String>?, oldValue: String?, newValue: String? -> loadDistributionChartData() }
        Platform.runLater { variableExpensesDistributionMonthComboBox!!.selectionModel.select(0) }
    }

    private fun loadDistributionChartData() {
        val variableExpensesData = FXCollections.observableArrayList<PieChart.Data>()
        for (category in categoryRoot!!.getCategoriesByClass(CategoryClass.Values.VARIABLE_EXPENSES)) {
            var amount: Amount
            amount = if (variableExpensesDistributionMonthComboBox!!.selectionModel.selectedIndex == 0) {
                category.getAmount(ValueDate())
            } else {
                val numberOfMonths = (1.5 * Math.pow(variableExpensesDistributionMonthComboBox!!.selectionModel.selectedIndex.toDouble(), 2.0) + 1.5 * balanceChartMonthComboBox!!.selectionModel.selectedIndex + 3).toInt()
                category.getAmount(TimeRange(LocalDate.now().minusMonths(numberOfMonths.toLong()), LocalDate.now()))
            }
            if (amount.isNotNull) {
                val data = PieChart.Data(category.name, Math.abs(amount.amount))
                Platform.runLater {
                    Tooltip.install(data.node,
                            Tooltip("""
    ${formatter.format(category)}
    ${get("amount")}: 	${formatter.format(data.pieValue)}
    """.trimIndent()))
                }
                variableExpensesData.add(data)
            }
        }
        variableExpensesDistributionPieChart!!.data = variableExpensesData
    }
}
package org.financer.client.javafx.main.transactions

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXTextField
import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import javafx.util.Callback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.financer.client.domain.api.RestApi
import org.financer.client.domain.api.RestApiImpl
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.category.CategoryRoot
import org.financer.client.domain.model.transaction.FixedTransaction
import org.financer.client.domain.model.transaction.VariableTransaction
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N.get
import org.financer.client.javafx.dialogs.FinancerConfirmDialog
import org.financer.client.javafx.format.JavaFXFormatter
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.client.javafx.main.FinancerController
import org.financer.client.local.Application
import org.financer.shared.domain.model.Settings
import org.financer.shared.domain.model.value.objects.SettingPair
import org.financer.shared.domain.model.value.objects.ValueDate
import org.financer.util.collections.Action
import org.financer.util.collections.Tree
import org.financer.util.date.Month
import java.net.URL
import java.time.LocalDate
import java.util.*
import java.util.function.Predicate
import java.util.logging.Logger

class TransactionsController : Initializable {
    @FXML
    var refreshTransactionsBtn: JFXButton? = null

    @FXML
    var newTransactionBtn: JFXButton? = null

    @FXML
    var editTransactionBtn: JFXButton? = null

    @FXML
    var deleteTransactionBtn: JFXButton? = null

    @FXML
    var transactionsTableView: TableView<VariableTransaction>? = null

    @FXML
    var filterTransactionsTextField: JFXTextField? = null

    @FXML
    var refreshFixedTransactionsBtn: JFXButton? = null

    @FXML
    var newFixedTransactionBtn: JFXButton? = null

    @FXML
    var editFixedTransactionBtn: JFXButton? = null

    @FXML
    var deleteFixedTransactionBtn: JFXButton? = null

    @FXML
    var categoriesListView: JFXListView<Category>? = null

    @FXML
    var fixedTransactionsListView: JFXListView<FixedTransaction>? = null

    @FXML
    var transactionsOverviewTableView: TableView<TransactionOverviewRow>? = null
    private val restApi: RestApi = RestApiImpl()
    private var user: User? = null
    private val logger = Logger.getLogger("FinancerApplication")
    private val localStorage = LocalStorageImpl.getInstance() as LocalStorageImpl
    private lateinit var categoryRoot: CategoryRoot
    private val formatter = JavaFXFormatter(localStorage)
    private var transactions: MutableList<VariableTransaction> = ArrayList()
    override fun initialize(location: URL, resources: ResourceBundle) {
        CoroutineScope(Dispatchers.Default).launch {
            FinancerController.instance!!.showLoadingBox()
            user = localStorage.readObject("user")
            categoryRoot = localStorage.readObject("categories")
                    ?: throw IllegalStateException("No categories stored in local storage.");
            editTransactionBtn!!.isDisable = true
            deleteTransactionBtn!!.isDisable = true
            newFixedTransactionBtn!!.isDisable = true
            editFixedTransactionBtn!!.isDisable = true
            deleteFixedTransactionBtn!!.isDisable = true
            initializeTransactionsTable()
            loadTransactionTableData()
            initializeFixedTransactionTable()
            loadFixedTransactionTableData()
            initializeTransactionsOverviewTable()
            loadTransactionOverviewTableData()
            FinancerController.instance!!.hideLoadingBox()
        }
    }

    private fun initializeTransactionsOverviewTable() {
        val numberOfMaxMonths = user!!.getValueOrDefault<Int>(SettingPair.Property.MAX_NUMBER_OF_MONTHS_DISPLAYED)
        val monthColumns: MutableList<TableColumn<TransactionOverviewRow, String>> = ArrayList(numberOfMaxMonths)
        val categoryColumn = TableColumn<TransactionOverviewRow, Category>(get("category"))
        categoryColumn.setCellValueFactory { param: TableColumn.CellDataFeatures<TransactionOverviewRow, Category> -> ReadOnlyObjectWrapper(param.value.category) }
        categoryColumn.setCellFactory({
            object : TableCell<TransactionOverviewRow, Category>() {
                override fun updateItem(item: Category, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        graphic = null
                    } else {
                        val categoryLabel: Label
                        if (item.isRoot) {
                            categoryLabel = Label((item.categoryClass!!.categoryClass.ordinal + 1).toString() + ". " + get(item.categoryClass!!.categoryClass.getName()))
                            categoryLabel.style = "-fx-font-weight: 700"
                        } else {
                            categoryLabel = Label(formatter.format(item))
                        }
                        graphic = categoryLabel
                    }
                }
            }
        })
        adjustColumnWidth(categoryColumn, transactionsOverviewTableView, 4.0)
        categoryColumn.isSortable = false
        for (i in 0 until numberOfMaxMonths) {
            val column = TableColumn<TransactionOverviewRow, String>(get(Objects.requireNonNull(Month.getMonthByNumber(LocalDate.now().minusMonths(i.toLong()).monthValue)).getName()))
            adjustColumnWidth(column, transactionsOverviewTableView, numberOfMaxMonths + 2.0)
            column.style = "-fx-alignment: CENTER-RIGHT;"
            column.isSortable = false
            column.setCellValueFactory { param: TableColumn.CellDataFeatures<TransactionOverviewRow, String> -> SimpleStringProperty(java.lang.Double.toString(param.value.amounts[i])) }
            column.setCellFactory { param: TableColumn<TransactionOverviewRow, String>? ->
                object : TableCell<TransactionOverviewRow, String>() {
                    override fun updateItem(item: String, empty: Boolean) {
                        super.updateItem(item, empty)
                        //                    setGraphic(!empty ? formatter.format(Double.valueOf(item)) : null);
                    }
                }
            }
            monthColumns.add(column)
        }
        Platform.runLater {
            transactionsOverviewTableView!!.columns.add(categoryColumn)
            transactionsOverviewTableView!!.columns.addAll(monthColumns)
        }
    }

    private fun initializeTransactionsTable() {
        val categoryColumn = TableColumn<VariableTransaction, Category>(get("category"))
        val valueDateColumn = TableColumn<VariableTransaction, LocalDate>(get("valueDate"))
        val amountColumn = TableColumn<VariableTransaction, Double>(get("amount"))
        val productColumn = TableColumn<VariableTransaction, String>(get("product"))
        val purposeColumn = TableColumn<VariableTransaction, String>(get("purpose"))
        val shopColumn = TableColumn<VariableTransaction, String>(get("shop"))
        valueDateColumn.cellValueFactory = PropertyValueFactory("valueDate")
        valueDateColumn.isSortable = true
        valueDateColumn.setComparator { obj: LocalDate, other: LocalDate? -> obj.compareTo(other) }
        valueDateColumn.style = "-fx-alignment: CENTER;"
        valueDateColumn.setCellFactory {
            object : TableCell<VariableTransaction, LocalDate>() {
                override fun updateItem(item: LocalDate, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (!empty) formatter.format(item) else null
                }
            }
        }
        amountColumn.cellValueFactory = PropertyValueFactory("amount")
        amountColumn.style = "-fx-alignment: CENTER-RIGHT;"
        amountColumn.setCellFactory(Callback {
            object : TableCell<VariableTransaction, Double>() {
                override fun updateItem(item: Double, empty: Boolean) {
                    super.updateItem(item, empty)
                    //                setGraphic(!empty ? formatter.format(item) : null);
                }
            }
        })
        categoryColumn.setCellValueFactory(PropertyValueFactory("categoryTree"))
        productColumn.setCellValueFactory(PropertyValueFactory("product"))
        purposeColumn.setCellValueFactory(PropertyValueFactory("purpose"))
        shopColumn.setCellValueFactory(PropertyValueFactory("shop"))
        adjustColumnWidth(valueDateColumn, transactionsTableView, 6.0)
        adjustColumnWidth(amountColumn, transactionsTableView, 6.0)
        adjustColumnWidth(categoryColumn, transactionsTableView, 6.0)
        adjustColumnWidth(productColumn, transactionsTableView, 6.0)
        adjustColumnWidth(purposeColumn, transactionsTableView, 6.0)
        adjustColumnWidth(shopColumn, transactionsTableView, 6.0)
        Platform.runLater {
            transactionsTableView!!.columns.add(categoryColumn)
            transactionsTableView!!.columns.add(valueDateColumn)
            transactionsTableView!!.columns.add(amountColumn)
            transactionsTableView!!.columns.add(productColumn)
            transactionsTableView!!.columns.add(purposeColumn)
            transactionsTableView!!.columns.add(shopColumn)
        }
        transactionsTableView!!.setRowFactory { param: TableView<VariableTransaction>? ->
            val row = TableRow<VariableTransaction>()
            row.onMouseClicked = EventHandler { event: MouseEvent ->
                if (event.clickCount == 2 && !row.isEmpty) {
                    handleEditTransaction()
                }
            }
            row
        }
        transactionsTableView!!.selectionModel.selectedItemProperty().addListener { observable: ObservableValue<out VariableTransaction>?, oldValue: VariableTransaction?, newValue: VariableTransaction? ->
            editTransactionBtn!!.isDisable = false
            deleteTransactionBtn!!.isDisable = false
        }
        Platform.runLater {
            transactionsTableView!!.columns[1].sortType = TableColumn.SortType.DESCENDING
            transactionsTableView!!.sortOrder.add(valueDateColumn)
        }
    }

    private fun initializeFixedTransactionTable() {
        categoriesListView!!.setCellFactory { param: ListView<Category>? -> CategoryListViewImpl() }
        categoriesListView!!.selectionModel.selectedItemProperty().addListener { observable: ObservableValue<out Category>?, oldValue: Category?, newValue: Category? ->
            showFixedTransactions(newValue)
            newFixedTransactionBtn!!.isDisable = !(newValue != null && !newValue.isRoot)
            editFixedTransactionBtn!!.isDisable = true
            deleteFixedTransactionBtn!!.isDisable = true
        }
        fixedTransactionsListView!!.selectionModel.selectedItemProperty().addListener { observable: ObservableValue<out FixedTransaction>?, oldValue: FixedTransaction?, newValue: FixedTransaction? ->
            editFixedTransactionBtn!!.isDisable = false
            deleteFixedTransactionBtn!!.isDisable = false
        }
        fixedTransactionsListView!!.onMouseClicked = EventHandler { mouseEvent: MouseEvent ->
            if (mouseEvent.clickCount == 2) {
                handleEditFixedTransaction()
            }
        }
        categoriesListView!!.setCellFactory { param: ListView<Category>? -> CategoryListViewImpl() }
    }

    private fun loadTransactionOverviewTableData() {
        transactionsOverviewTableView!!.items.clear()
        val rows: MutableMap<Category, TransactionOverviewRow> = HashMap()
        if (categoryRoot != null) {
            categoryRoot!!.traverse { category: Tree ->
                val transactionOverviewRow = TransactionOverviewRow(category as Category, user)
                for (i in 0..5) {
                    transactionOverviewRow.amounts[i] = category.getAmount(ValueDate(LocalDate.now().minusMonths(i.toLong()))).amount
                }
                rows[category] = transactionOverviewRow
            }
        }
        val items: List<TransactionOverviewRow> = ArrayList(rows.values)
        items.sortedWith(Comparator { o1: TransactionOverviewRow, o2: TransactionOverviewRow ->
            java.lang.String.CASE_INSENSITIVE_ORDER.compare(formatter.format(o1.category),
                    formatter.format(o2.category))
        })
        transactionsOverviewTableView!!.items.addAll(items)
        transactionsOverviewTableView!!.refresh()
    }

    private fun loadTransactionTableData() {
        val transactions = FXCollections.observableArrayList<VariableTransaction>()
        if (categoryRoot != null) {
            categoryRoot!!.traverse { treeItem: Tree ->
                val category = treeItem as Category
                if (!category.isFixed) {
                    for (abstractTransaction in category.transactions) {
                        if (abstractTransaction is VariableTransaction) {
                            transactions.add(abstractTransaction)
                        }
                    }
                }
            }
        }
        val filteredData = FilteredList(transactions, Predicate { transaction: VariableTransaction? -> true })
        filterTransactionsTextField!!.textProperty().addListener { observable: ObservableValue<out String?>?, oldValue: String?, newValue: String? ->
            filteredData.setPredicate { transaction: VariableTransaction ->
                if (newValue == null || newValue.isEmpty()) {
                    return@setPredicate true
                }
                transaction.vendor!!.toLowerCase().contains(newValue.toLowerCase()) ||
                        transaction.category!!.name!!.toLowerCase().contains(newValue.toLowerCase()) ||
                        transaction.description!!.toLowerCase().contains(newValue.toLowerCase())
            }
        }
        val sortedData = SortedList(filteredData)
        sortedData.comparatorProperty().bind(transactionsTableView!!.comparatorProperty())
        transactionsTableView!!.items = sortedData
        transactionsTableView!!.refresh()
    }

    private fun loadFixedTransactionTableData() {
        categoriesListView!!.items.clear()
        categoryRoot.traverse { treeItem: Tree ->
            val category = treeItem as Category
            if (category.categoryClass!!.isFixed) {
                categoriesListView!!.items.add(category)
            }
        }
        Platform.runLater {
            categoriesListView!!.items.sortWith(Comparator { o1: Category?, o2: Category? ->
                java.lang.String.CASE_INSENSITIVE_ORDER.compare(formatter.format(o1!!),
                        formatter.format(o2!!))
            })
        }
    }

    fun handleRefreshTransactions() = CoroutineScope(Dispatchers.IO).launch {
        val result = restApi.getUsersVariableTransactions(user!!.getId(), 0)
        transactions = result.toMutableList()
        withContext(Dispatchers.Main) {
            loadTransactionTableData()
            transactionsTableView!!.columns[1].sortType = TableColumn.SortType.DESCENDING
            transactionsTableView!!.sortOrder.add(transactionsTableView!!.columns[1])
            transactionsTableView!!.refresh()
            loadTransactionOverviewTableData()
        }
    }

    fun handleRefreshFixedTransactions() = CoroutineScope(Dispatchers.IO).launch {
        restApi.getUsersFixedTransactions(user!!.getId())
    }

    fun handleNewTransaction() {
        val dialog = TransactionDialog(null, categoryRoot)
        dialog.setOnConfirm(Action { transaction: VariableTransaction? ->
            if (transaction != null) {
                if (user!!.getValueOrDefault(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY)) {
                    transaction.adjustAmountSign()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val result = restApi.createVariableTransaction(transaction)
                    if (result != null) {
                        transactions.add(result)
                        localStorage.writeObject("categories", categoryRoot)
                        FinancerController.instance!!.showToast(Application.MessageType.SUCCESS, get("succAddedTransaction"))
                        withContext(Dispatchers.Main) {
                            loadTransactionTableData()
                            loadTransactionOverviewTableData()
                        }
                    }
                }
            }
        })
    }

    fun handleNewFixedTransaction() {
        val dialog = FixedTransactionDialog(null,
                categoriesListView!!.selectionModel.selectedItem)
        dialog.setOnConfirm(Action { fixedTransaction: FixedTransaction? ->
            if (fixedTransaction != null) {
                if (user!!.getValueOrDefault(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY)) {
                    fixedTransaction.adjustAmountSign()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    restApi.createFixedTransaction(fixedTransaction)
                    handleRefreshFixedTransactions()
                    FinancerController.instance!!.showToast(Application.MessageType.SUCCESS, get("succAddedFixedTransaction"))
                    withContext(Dispatchers.Main) { loadTransactionOverviewTableData() }
                }
            }
        })
    }

    fun handleEditTransaction() {
        val dialog = TransactionDialog(transactionsTableView!!.selectionModel.selectedItem,
                categoryRoot)
        dialog.setOnConfirm(Action { transaction: VariableTransaction? ->
            if (transaction != null) {
                if (user!!.getValueOrDefault(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY)) {
                    transaction.adjustAmountSign()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    restApi.updateVariableTransaction(transaction)
                    localStorage.writeObject("categories", categoryRoot)
                    Platform.runLater { transactionsTableView!!.refresh() }
                    FinancerController.instance!!.showToast(Application.MessageType.SUCCESS, get("succUpdatedTransaction"))
                    Platform.runLater { loadTransactionOverviewTableData() }
                }
            }
        })
    }

    fun handleEditFixedTransaction() {
        val dialog = FixedTransactionDialog(
                fixedTransactionsListView!!.selectionModel.selectedItem,
                categoriesListView!!.selectionModel.selectedItem)
        dialog.setOnConfirm(Action { fixedTransaction: FixedTransaction? ->
            if (fixedTransaction != null) {
                if (user!!.getValueOrDefault(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY)) {
                    fixedTransaction.adjustAmountSign()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val result = restApi.updateFixedTransaction(fixedTransaction)
                    if (result != null) {
                        fixedTransaction.getTransactionAmounts().clear()
                        fixedTransaction.getTransactionAmounts().addAll(result.getTransactionAmounts())
                        localStorage.writeObject("categories", categoryRoot)
                        FinancerController.instance!!.showToast(Application.MessageType.SUCCESS, get("succUpdatedFixedTransaction"))
                        withContext(Dispatchers.Main) {
                            fixedTransactionsListView!!.refresh()
                            categoriesListView!!.refresh()
                            loadTransactionOverviewTableData()
                        }
                    }
                }
            }
        })
    }

    fun handleDeleteTransaction() {
        val dialog = FinancerConfirmDialog(get("confirmDeleteTransaction"))
        dialog.setOnConfirm(Action {
            val transaction = transactionsTableView!!.selectionModel.selectedItem
            if (transaction != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    restApi.deleteVariableTransaction(transaction.id)
                    withContext(Dispatchers.Main) {
                        transaction.category!!.transactions.remove(transaction)
                        loadTransactionTableData()
                        localStorage.writeObject("categories", categoryRoot)
                    }
                    FinancerController.instance!!.showToast(Application.MessageType.SUCCESS, get("succDeletedTransaction"))
                }
            }
        })
    }

    fun handleDeleteFixedTransaction() {
        val dialog = FinancerConfirmDialog(get("confirmDeleteFixedTransaction"))
        dialog.setOnConfirm(Action { result: Boolean? ->
            val fixedTransactionId = fixedTransactionsListView!!.selectionModel.selectedItem.id
            CoroutineScope(Dispatchers.IO).launch {
                restApi.deleteFixedTransaction(fixedTransactionId)
                Platform.runLater { fixedTransactionsListView!!.items.remove(fixedTransactionsListView!!.selectionModel.selectedItem) }
                localStorage.writeObject("categories", categoryRoot)
                fixedTransactionsListView!!.refresh()
                categoriesListView!!.refresh()
                FinancerController.instance!!.showToast(Application.MessageType.SUCCESS, get("succDeletedFixedTransaction"))
            }
        })
    }

    private fun showFixedTransactions(category: Category?) {
        if (category != null) {
            fixedTransactionsListView!!.items.clear()
            for (abstractTransaction in category.transactions) {
                if (abstractTransaction is FixedTransaction) {
                    fixedTransactionsListView!!.items.add(abstractTransaction)
                }
            }
        }
        fixedTransactionsListView!!.setCellFactory { FixedTransactionListCellImpl() }
        fixedTransactionsListView!!.items.sortWith(compareByDescending { t: FixedTransaction -> t.timeRange })
    }

    private fun <S, T> adjustColumnWidth(column: TableColumn<S, T>, tableView: TableView<S>?, ratio: Double) {
        column.prefWidthProperty().bind(tableView!!.widthProperty().divide(ratio).add(-3))
    }

    private inner class CategoryListViewImpl : ListCell<Category>() {
        private var borderPane: BorderPane? = null
        private var categoryLabel: Label? = null
        private var amountLabel: Label? = null
        override fun updateItem(item: Category, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty) {
                graphic = null
            } else {
                initListCell()
                categoryLabel!!.text = formatter.format(item)
                formatter.format(amountLabel!!, item.getAmount(ValueDate()))
                if (item.isRoot) {
                    categoryLabel!!.styleClass.add("list-cell-title")
                }
            }
            graphic = borderPane
        }

        private fun initListCell() {
            borderPane = BorderPane()
            borderPane!!.styleClass.add("categories-list-item")
            categoryLabel = Label()
            amountLabel = Label()
            amountLabel!!.styleClass.add("list-cell-title")
            borderPane!!.left = categoryLabel
            borderPane!!.right = amountLabel
        }
    }

    private inner class FixedTransactionListCellImpl : ListCell<FixedTransaction>() {
        private var borderPane: BorderPane? = null
        private var activeLabel: Label? = null
        private var dateLabel: Label? = null
        private var amountLabel: Label? = null
        private var isVariableLabel: Label? = null
        private var dayLabel: Label? = null
        private var lastAmountLabel: Label? = null
        private var preLastAmountLabel: Label? = null
        override fun updateItem(item: FixedTransaction, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty) {
                graphic = null
            } else {
                initListCell(item)
                if (item.isActive) {
                    activeLabel!!.text = get("active")
                    activeLabel!!.styleClass.add("pos-amount")
                } else {
                    activeLabel!!.text = get("inactive")
                }
                dateLabel!!.text = formatter.format(item.timeRange!!)
                val valueDate = item.timeRange!!.endDateOrCurrentDate
                formatter.format(amountLabel!!, item.getAmount(ValueDate(valueDate)))
                if (item.isVariable) {
                    formatter.format(lastAmountLabel!!, item.getAmount(ValueDate(valueDate.minusMonths(1))))
                    formatter.format(preLastAmountLabel!!, item.getAmount(ValueDate(valueDate.minusMonths(2))))
                }
                isVariableLabel!!.text = get("isVariable") + ": " +
                        if (item.isVariable) get("yes") else get("no")
                dayLabel!!.text = get("valueDate") + ": " + item.day
                graphic = borderPane
            }
        }

        private fun initListCell(item: FixedTransaction) {
            borderPane = BorderPane()
            borderPane!!.styleClass.add("transactions-list-item")
            val vBoxLeft = VBox()
            activeLabel = Label()
            activeLabel!!.styleClass.add("list-cell-title")
            vBoxLeft.children.add(activeLabel)
            isVariableLabel = Label()
            vBoxLeft.children.add(isVariableLabel)
            dayLabel = Label()
            vBoxLeft.children.add(dayLabel)
            dateLabel = Label()
            dateLabel!!.textAlignment = TextAlignment.CENTER
            val vBoxRight = VBox()
            amountLabel = Label()
            amountLabel!!.alignment = Pos.CENTER_RIGHT
            amountLabel!!.styleClass.add("list-cell-title")
            vBoxRight.children.add(amountLabel)
            if (item.isVariable) {
                lastAmountLabel = Label()
                lastAmountLabel!!.alignment = Pos.CENTER_RIGHT
                vBoxRight.children.add(lastAmountLabel)
                preLastAmountLabel = Label()
                preLastAmountLabel!!.alignment = Pos.CENTER_RIGHT
                vBoxRight.children.add(preLastAmountLabel)
            }
            vBoxRight.alignment = Pos.CENTER_RIGHT
            borderPane!!.left = vBoxLeft
            borderPane!!.center = dateLabel
            borderPane!!.right = vBoxRight
            BorderPane.setAlignment(borderPane!!.center, Pos.TOP_CENTER)
            BorderPane.setAlignment(borderPane!!.right, Pos.CENTER_RIGHT)
        }
    }

    class TransactionOverviewRow internal constructor(val category: Category, settings: Settings?) {
        val amounts: DoubleArray

        init {
            amounts = settings!!.getValueOrDefault(SettingPair.Property.MAX_NUMBER_OF_MONTHS_DISPLAYED)
        }
    }
}
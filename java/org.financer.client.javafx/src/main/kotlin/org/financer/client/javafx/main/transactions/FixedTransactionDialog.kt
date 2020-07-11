package org.financer.client.javafx.main.transactions

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXTextField
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.*
import javafx.util.Callback
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.transaction.FixedTransaction
import org.financer.client.domain.model.transaction.FixedTransactionAmount
import org.financer.client.format.Formatter
import org.financer.client.format.FormatterImpl
import org.financer.client.format.I18N.get
import org.financer.client.javafx.components.DatePicker
import org.financer.client.javafx.components.DoubleField
import org.financer.client.javafx.components.IntegerField
import org.financer.client.javafx.dialogs.FinancerConfirmDialog
import org.financer.client.javafx.dialogs.FinancerDialog
import org.financer.client.javafx.format.JavaFXFormatter
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.TimeRange
import org.financer.util.collections.Action
import java.time.LocalDate
import java.util.*

class FixedTransactionDialog internal constructor(value: FixedTransaction?, private val category: Category) : FinancerDialog<FixedTransaction?>(value) {
    private var categoryLabel: Label? = null
    private var dayField: IntegerField? = null
    private var startDateField: DatePicker? = null
    private var endDateField: DatePicker? = null
    private var productField: JFXTextField? = null
    private var descriptionField: JFXTextField? = null
    private val vendorField: JFXTextField? = null
    private var isVariableCheckbox: JFXCheckBox? = null
    private var amountField: DoubleField? = null
    private var transactionAmountContainer: VBox? = null
    private var transactionAmountListView: JFXListView<FixedTransactionAmount>? = null
    override val dialogContent: Region
        get() {
            val hBox = HBox()
            hBox.spacing = 30.0
            val gridPane = GridPane()
            gridPane.hgap = 120.0
            gridPane.vgap = 10.0
            gridPane.add(Label(get("category")), 0, 0)
            categoryLabel = Label()
            gridPane.add(categoryLabel, 1, 0)
            gridPane.add(Label(get("valueDate")), 0, 1)
            dayField = IntegerField()
            dayField!!.id = "dayTextField"
            dayField!!.value = 1
            gridPane.add(dayField, 1, 1)
            gridPane.add(Label(get("startDate")), 0, 2)
            startDateField = DatePicker(formatter)
            startDateField!!.id = "startDateDatePicker"
            startDateField!!.value = LocalDate.now()
            gridPane.add(startDateField, 1, 2)
            gridPane.add(Label(get("endDate")), 0, 3)
            endDateField = DatePicker(formatter)
            endDateField!!.id = "endDateDatePicker"
            gridPane.add(endDateField, 1, 3)
            gridPane.add(Label(get("isVariable")), 0, 4)
            isVariableCheckbox = JFXCheckBox()
            isVariableCheckbox!!.id = "isVariableCheckbox"
            isVariableCheckbox!!.selectedProperty().addListener { _: ObservableValue<out Boolean?>?, _: Boolean?, newValue: Boolean? ->
                if (amountField != null) {
                    amountField!!.isDisable = newValue!!
                }
                if (content != null) {
                    toggleTransactionAmountContainer()
                }
            }
            gridPane.add(isVariableCheckbox, 1, 4)
            gridPane.add(Label(get("amount")), 0, 5)
            amountField = DoubleField()
            amountField!!.id = "amountTextField"
            gridPane.add(amountField, 1, 5)
            gridPane.add(Label(get("product")), 0, 6)
            productField = JFXTextField()
            productField!!.id = "productTextField"
            gridPane.add(productField, 1, 6)
            gridPane.add(Label(get("purpose")), 0, 7)
            descriptionField = JFXTextField()
            descriptionField!!.id = "purposeTextField"
            gridPane.add(descriptionField, 1, 7)
            hBox.children.add(gridPane)
            transactionAmountContainer = VBox()
            transactionAmountContainer!!.spacing = 10.0
            transactionAmountContainer!!.prefHeight = 200.0
            transactionAmountContainer!!.children.add(Label(get("transactionAmounts")))
            val fontAwesome = GlyphFontRegistry.font("FontAwesome")
            val newTransactionAmountBtn = JFXButton(get("new"), fontAwesome.create(FontAwesome.Glyph.PLUS))
            newTransactionAmountBtn.id = "newTransactionAmountBtn"
            val editTransactionAmountBtn = JFXButton(get("edit"), fontAwesome.create(FontAwesome.Glyph.EDIT))
            editTransactionAmountBtn.id = "editTransactionAmountBtn"
            editTransactionAmountBtn.isDisable = true
            val deleteTransactionAmountBtn = JFXButton(get("delete"), fontAwesome.create(FontAwesome.Glyph.TRASH))
            deleteTransactionAmountBtn.id = "deleteTransactionAmountBtn"
            deleteTransactionAmountBtn.isDisable = true
            newTransactionAmountBtn.onAction = EventHandler { event: ActionEvent? ->
                val dialog = TransactionAmountDialog(null, transactionAmountListView!!.items)
                dialog.setOnConfirm(Action { transactionAmount: FixedTransactionAmount? ->
                    if (transactionAmount != null) {
                        transactionAmount.setFixedTransaction(value)
                        transactionAmountListView!!.items.add(transactionAmount)
                        transactionAmountListView!!.items.sortWith(compareByDescending { t: FixedTransactionAmount -> t.valueDate })
                    }
                })
            }
            editTransactionAmountBtn.onAction = EventHandler { event: ActionEvent? ->
                if (transactionAmountListView!!.selectionModel.selectedItem != null) {
                    val dialog = TransactionAmountDialog(transactionAmountListView!!.selectionModel.selectedItem,
                            transactionAmountListView!!.items)
                    dialog.setOnConfirm(Action { result: FixedTransactionAmount? -> transactionAmountListView!!.refresh() })
                }
            }
            deleteTransactionAmountBtn.onAction = EventHandler { event: ActionEvent? ->
                val dialog = FinancerConfirmDialog(get("confirmDeleteTransactionAmount"))
                dialog.setOnConfirm(Action { result: Boolean? ->
                    if (transactionAmountListView!!.selectionModel.selectedItem != null) {
                        transactionAmountListView!!.items.remove(transactionAmountListView!!.selectionModel.selectedItem)
                    }
                })
            }
            val toolBox = HBox()
            toolBox.spacing = 8.0
            toolBox.children.add(newTransactionAmountBtn)
            toolBox.children.add(editTransactionAmountBtn)
            toolBox.children.add(deleteTransactionAmountBtn)
            transactionAmountContainer!!.children.add(toolBox)
            transactionAmountListView = JFXListView()
            transactionAmountListView!!.id = "transactionAmountListView"
            transactionAmountListView!!.selectionModel.selectedItemProperty().addListener { observableValue: ObservableValue<out FixedTransactionAmount?>?, oldValue: FixedTransactionAmount?, newValue: FixedTransactionAmount? ->
                if (newValue != null) {
                    editTransactionAmountBtn.isDisable = false
                    deleteTransactionAmountBtn.isDisable = false
                } else {
                    editTransactionAmountBtn.isDisable = true
                    deleteTransactionAmountBtn.isDisable = true
                }
            }
            transactionAmountListView!!.setCellFactory(Callback { param: ListView<FixedTransactionAmount>? ->
                object : ListCell<FixedTransactionAmount>() {
                    override fun updateItem(item: FixedTransactionAmount, empty: Boolean) {
                        super.updateItem(item, empty)
                        if (empty) {
                            graphic = null
                        } else {
                            val formatter = JavaFXFormatter(LocalStorageImpl.getInstance())
                            val borderPane = BorderPane()
                            borderPane.styleClass.add("transactions-list-item")
                            borderPane.left = Label(formatter.format(item.valueDate!!))
                            val amountLabel = formatter.format(Label(), item.amount)
                            borderPane.right = amountLabel
                            graphic = borderPane
                        }
                    }
                }
            })
            transactionAmountContainer!!.children.add(transactionAmountListView)
            hBox.children.add(transactionAmountContainer)
            return hBox
        }

    override val dialogWidth: Double
        get() = 750.0

    override fun prepareDialogContent() {
        categoryLabel!!.text = FormatterImpl(LocalStorageImpl.getInstance()).format(this.category)
        if (value != null) {
            dayField!!.value = value!!.day
            startDateField!!.value = value!!.timeRange!!.startDate
            endDateField!!.value = value!!.timeRange!!.endDate
            productField!!.text = value!!.product
            descriptionField!!.text = value!!.description
            vendorField!!.text = value!!.vendor
            isVariableCheckbox!!.isSelected = value!!.isVariable
            if (value!!.isVariable) {
                this.toggleTransactionAmountContainer(true)
                if (value!!.getTransactionAmounts().isNotEmpty()) {
                    transactionAmountListView!!.items.addAll(value!!.getTransactionAmounts())
                    transactionAmountListView!!.items.sortWith(Comparator { o1: FixedTransactionAmount, o2: FixedTransactionAmount -> o2.valueDate!!.compareTo(o1.valueDate) })
                }
                amountField!!.isDisable = true
            } else {
                amountField!!.text = java.lang.Double.toString(value!!.amount.amount)
                this.toggleTransactionAmountContainer(false)
            }
        } else {
            this.toggleTransactionAmountContainer(false)
        }
    }

    private fun toggleTransactionAmountContainer() {
        transactionAmountContainer!!.isManaged = !transactionAmountContainer!!.isManaged
        transactionAmountContainer!!.isVisible = !transactionAmountContainer!!.isVisible
    }

    private fun toggleTransactionAmountContainer(visible: Boolean) {
        transactionAmountContainer!!.isManaged = visible
        transactionAmountContainer!!.isVisible = visible
    }

    override fun checkConsistency(): Boolean {
        return true
    }

    override fun onConfirm() {
        if (value == null) {
            value = FixedTransaction()
                    .setId(0)
                    .setAmount(Amount(amountField!!.text.toDouble()))
                    .setCategory(category)
                    .setTimeRange(TimeRange(startDateField!!.value, endDateField!!.value))
                    .setDay(dayField!!.value)
                    .setProduct(productField!!.text)
                    .setDescription(descriptionField!!.text)
                    .setVendor(vendorField!!.text)
                    .setIsVariable(isVariableCheckbox!!.isSelected)
                    .setTransactionAmounts(if (isVariableCheckbox!!.isSelected) HashSet(transactionAmountListView!!.items) else null)
        } else {
            value!!.getTransactionAmounts().clear()
            value!!.getTransactionAmounts().addAll(ArrayList(transactionAmountListView!!.items))
            value!!.setTimeRange(TimeRange(startDateField!!.value, endDateField!!.value))
            value!!.setProduct(productField!!.text)
            value!!.setDescription(descriptionField!!.text)
            value!!.setVendor(vendorField!!.text)
            value!!.isVariable = isVariableCheckbox!!.isSelected
            value!!.setDay(dayField!!.value)
            value!!.setAmount(Amount(amountField!!.text.toDouble()))
        }
        super.onConfirm()
    }

    companion object {
        private val formatter: Formatter = JavaFXFormatter(LocalStorageImpl.getInstance())
    }

    init {
        prepareDialogContent()
        setDialogTitle(get("fixedTransactions"))
    }
}
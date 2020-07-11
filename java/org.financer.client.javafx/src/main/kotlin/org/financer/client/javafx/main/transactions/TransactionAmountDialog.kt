package org.financer.client.javafx.main.transactions

import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Region
import org.financer.client.domain.model.transaction.FixedTransactionAmount
import org.financer.client.format.I18N.get
import org.financer.client.javafx.components.DatePicker
import org.financer.client.javafx.components.DoubleField
import org.financer.client.javafx.dialogs.FinancerDialog
import org.financer.client.javafx.format.JavaFXFormatter
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.ValueDate
import java.time.LocalDate

class TransactionAmountDialog(value: FixedTransactionAmount?, private val transactionAmounts: List<FixedTransactionAmount?>) : FinancerDialog<FixedTransactionAmount?>(value) {
    private var valueDateField: DatePicker? = null
    private var amountField: DoubleField? = null
    override val dialogContent: Region
        get() {
            val gridPane = GridPane()
            gridPane.hgap = 120.0
            gridPane.vgap = 10.0
            gridPane.add(Label(get("valueDate")), 0, 0)
            valueDateField = DatePicker(JavaFXFormatter(LocalStorageImpl.getInstance()))
            valueDateField!!.value = LocalDate.now()
            valueDateField!!.id = "transactionAmountValueDatePicker"
            gridPane.add(valueDateField, 1, 0)
            gridPane.add(Label(get("amount")), 0, 1)
            amountField = DoubleField()
            amountField!!.id = "transactionAmountTextField"
            gridPane.add(amountField, 1, 1)
            return gridPane
        }

    override fun prepareDialogContent() {
        if (value != null) {
            valueDateField!!.value = value!!.valueDate!!.date
            amountField!!.text = java.lang.Double.toString(value!!.amount.amount)
        }
    }

    override fun checkConsistency(): Boolean {
        var result = true
        if (value == null) {
            for (transactionAmount in transactionAmounts) {
                if (transactionAmount!!.valueDate!!.isInSameMonth(ValueDate(valueDateField!!.value))) {
                    errorMessage = get("errTransactionAmountExists")
                    result = false
                    break
                }
            }
        }
        return result
    }

    override fun onConfirm() {
        if (value == null) {
            value = FixedTransactionAmount()
                    .setId(0)
                    .setAmount(Amount(amountField!!.text.toDouble()))
                    .setValueDate(ValueDate(valueDateField!!.value))
        } else {
            value!!.setValueDate(ValueDate(valueDateField!!.value))
            value!!.setAmount(Amount(amountField!!.text.toDouble()))
        }
        super.onConfirm()
    }

    init {
        prepareDialogContent()
        setDialogTitle(get("transactionAmounts"))
    }
}
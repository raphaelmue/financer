package org.financer.client.javafx.main

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXListView
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import org.financer.client.domain.model.transaction.FixedTransaction
import org.financer.client.domain.model.transaction.FixedTransactionAmount
import org.financer.client.format.I18N.get
import org.financer.client.javafx.components.DoubleField
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.ValueDate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integration")
class FixedTransactionTest : AbstractFinancerApplicationTest() {
    @BeforeEach
    @Throws(Exception::class)
    public override fun setUpEach() {
        super.setUpEach()
    }

    @Test
    fun testAddFixedTransaction() {
        val fixedTransaction = fixedTransaction()
        register(user(), password())
        addCategory(fixedCategory())
        addFixedTransaction(fixedTransaction!!)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#categoriesListView") as JFXListView<*>)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        Assertions.assertNotNull(find { label: Label? -> label!!.text.contains(get("active")) })
        Assertions.assertNotNull(find { label: Label? -> label!!.text.contains(formatter!!.format(fixedTransaction.amount)!!) })
        Assertions.assertNotNull(find { label: Label? ->
            label!!.text.contains(get("since") + " " +
                    formatter!!.format(fixedTransaction.timeRange!!.startDate))
        })
    }

    @Test
    fun testEditFixedTransaction() {
        val fixedTransaction = fixedTransaction()
        register(user(), password())
        addCategory(fixedCategory())
        addFixedTransaction(fixedTransaction!!)
        val amount = fixedTransaction.amount.divide(2.0)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#categoriesListView") as JFXListView<*>)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        clickOn(find { label: Label? -> label!!.text.contains(get("active")) })
        clickOn(find<Node>("#editFixedTransactionBtn") as JFXButton)
        clickOn(find<Node>("#amountTextField") as DoubleField)
        eraseText(6)
        write(java.lang.Double.toString(amount.amount))
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        Assertions.assertNotNull(find { label: Label? -> label!!.text.contains(formatter!!.format(amount)!!) })
        //        Assertions.assertEquals(1, ((Category) TreeUtil.getByValue(((CategoryRoot) LocalStorageImpl.getInstance().readObject("categories"))
//                        .getCategoriesByClass(fixedTransaction.getCategoryTree().getValue().getCategoryClass()),
//                fixedTransaction.getCategoryTree(), (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()))).getTransactions().size());
    }

    @Test
    fun testDeleteFixedTransaction() {
        register(user(), password())
        addCategory(fixedCategory())
        addFixedTransaction(fixedTransaction())
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#categoriesListView") as JFXListView<*>)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        clickOn(find { label: Label? -> label!!.text.contains(get("active")) })
        clickOn(find<Node>("#deleteFixedTransactionBtn") as JFXButton)
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#categoriesListView") as JFXListView<*>)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        Assertions.assertEquals(0, (find<Node>("#fixedTransactionsListView") as JFXListView<*>).items.size)
        Assertions.assertNotNull(find { label: Label? -> label!!.text.contains(formatter!!.format(Amount())!!) })
    }

    // ------------------ VARIABLE ------------------ \\
    @Test
    fun testAddFixedVariableTransaction() {
        val fixedTransaction = fixedTransaction()
        fixedTransaction!!.isVariable = true
        register(user(), password())
        addCategory(fixedCategory())
        addFixedTransaction(fixedTransaction)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#refreshFixedTransactionsBtn") as JFXButton)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find { label: Label? -> label!!.text.contains(fixedTransaction.category!!.name!!) })
        Assertions.assertNotNull(find { label: Label? -> label!!.text.contains(get("active")) })
        Assertions.assertEquals(1, fixedTransaction.getTransactionAmounts().size)
        for (transactionAmount in fixedTransaction.getTransactionAmounts()) {
            Assertions.assertEquals(450.0, transactionAmount.amount.amount)
        }
        // TODO: works locally, but not on server; needs to be fixed
        // Assertions.assertNotNull(find((Label label) -> label.getText().contains(Formatter.formatCurrency(
        //        fixedTransaction.getTransactionAmounts().get(0).getAmount()))));
    }

    @Test
    @Tag("skip")
    fun testEditFixedVariableTransactionEditTransactionAmount() {
        val amount = 450.0
        val fixedTransaction = fixedTransaction()
        fixedTransaction!!.isVariable = true
        register(user(), password())
        addCategory(fixedCategory())
        addFixedTransaction(fixedTransaction)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        clickOn(find { label: Label? -> label!!.text.contains(fixedTransaction.category!!.name!!) })
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find { label: Label? -> label!!.text.contains(get("active")) })
        clickOn(find<Node>("#editFixedTransactionBtn") as JFXButton)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertTrue(find<Node>("#editTransactionAmountBtn").isDisabled)
        Assertions.assertTrue(find<Node>("#deleteTransactionAmountBtn").isDisabled)
        Assertions.assertTrue((find<Node>("#isVariableCheckbox") as CheckBox).isSelected)
        val transactionAmountListView = find<JFXListView<FixedTransactionAmount>>("#transactionAmountListView")
        Assertions.assertTrue(transactionAmountListView.isManaged)
        transactionAmountListView.selectionModel.select(0)
        clickOn(find<Node>("#editTransactionAmountBtn") as JFXButton)
        clickOn(find<Node>("#transactionAmountTextField") as DoubleField)
        eraseText(6)
        write(java.lang.Double.toString(amount / 2))
        press(KeyCode.TAB).release(KeyCode.TAB)
        press(KeyCode.ENTER).release(KeyCode.ENTER)
        Assertions.assertNotNull(clickOn(formatter!!.format(Amount(amount / 2))))
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        var updatedTransaction: FixedTransaction
        for (transaction in categoryTree.transactions) {
            if (transaction is FixedTransaction) {
                updatedTransaction = transaction
                Assertions.assertNotNull(updatedTransaction)
                Assertions.assertEquals(amount / 2, updatedTransaction.getAmount(ValueDate()).amount)
            }
        }
    }

    @Test
    @Tag("skip")
    fun testEditFixedVariableTransactionDeleteTransactionAmount() {
        val fixedTransaction = fixedTransaction()
        fixedTransaction!!.isVariable = true
        register(user(), password())
        addCategory(fixedCategory())
        addFixedTransaction(fixedTransaction)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        clickOn(find { label: Label? -> label!!.text.contains(fixedTransaction.category!!.name!!) })
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find { label: Label? -> label!!.text.contains(get("active")) })
        clickOn(find<Node>("#editFixedTransactionBtn") as JFXButton)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertTrue(find<Node>("#editTransactionAmountBtn").isDisabled)
        Assertions.assertTrue(find<Node>("#deleteTransactionAmountBtn").isDisabled)
        Assertions.assertTrue((find<Node>("#isVariableCheckbox") as CheckBox).isSelected)
        var transactionAmountListView = find<JFXListView<FixedTransactionAmount?>>("#transactionAmountListView")
        Assertions.assertTrue(transactionAmountListView.isManaged)
        transactionAmountListView.selectionModel.select(0)
        clickOn(find<Node>("#deleteTransactionAmountBtn") as JFXButton)
        press(KeyCode.ENTER).release(KeyCode.ENTER)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        transactionAmountListView = find("#transactionAmountListView")
        Assertions.assertEquals(0, transactionAmountListView.items.size)
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        var updatedTransaction: FixedTransaction
        for (transaction in categoryTree.transactions) {
            if (transaction is FixedTransaction) {
                updatedTransaction = transaction
                Assertions.assertNotNull(updatedTransaction)
                Assertions.assertEquals(0, updatedTransaction.getTransactionAmounts().size)
            }
        }
    }
}
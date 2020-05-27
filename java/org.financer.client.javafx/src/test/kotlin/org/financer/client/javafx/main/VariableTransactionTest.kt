package org.financer.client.javafx.main

import com.jfoenix.controls.JFXButton
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.category.CategoryRoot
import org.financer.client.domain.model.transaction.VariableTransaction
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.util.collections.TreeUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.*

@Tag("integration")
class VariableTransactionTest : AbstractFinancerApplicationTest() {
    @BeforeEach
    @Throws(Exception::class)
    public override fun setUpEach() {
        super.setUpEach()
    }

    @Test
    fun testAddTransaction() {
        val transaction = variableTransaction()
        register(user(), password())
        addCategory(variableCategory())
        addVariableTransaction(transaction!!)
        clickOn(find<Node>("#refreshTransactionsBtn") as Button)
        sleep(1000)
        Assertions.assertNotNull(clickOn(formatter!!.format(transaction.amount)))
        val categoryRoot = LocalStorageImpl.getInstance().readObject<CategoryRoot>("categories")
        val transactions = (TreeUtil.getByValue(categoryRoot, transaction.category,
                Comparator.comparingLong(Category::id)) as Category).transactions
        Assertions.assertEquals(1, transactions.size)
        for (_transaction in transactions) {
            Assertions.assertTrue(_transaction is VariableTransaction)
            Assertions.assertTrue(_transaction.id > 0)
        }
    }

    @Test
    fun testEditTransaction() {
        val transaction = variableTransaction()
        register(user(), password())
        addCategory(variableCategory())
        addVariableTransaction(transaction!!)
        clickOn(formatter!!.format(transaction.valueDate!!))
        clickOn(find<Node>("#editTransactionBtn") as Button)
        val amount = transaction.amount.divide(2.0)
        val amountTextField = find<TextField>("#amountTextField")
        clickOn(amountTextField)
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE)
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE)
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE)
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE)
        write(java.lang.Double.toString(amount.amount))
        confirmDialog()
        clickOn(find<Node>("#refreshTransactionsBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertNotNull(clickOn(formatter!!.format(amount)))
        val categoryRoot = LocalStorageImpl.getInstance().readObject<CategoryRoot>("categories")
        val transactions = (TreeUtil.getByValue(categoryRoot, transaction.category,
                Comparator.comparingLong(Category::id)) as Category).transactions
        Assertions.assertEquals(1, transactions.size)
        for (_transaction in transactions) {
            Assertions.assertTrue(_transaction is VariableTransaction)
            Assertions.assertTrue(_transaction.id > 0)
            Assertions.assertEquals(amount, _transaction.amount)
        }
    }

    @Test
    fun testDeleteTransaction() {
        val transaction = variableTransaction()
        register(user(), password())
        addCategory(variableCategory())
        addVariableTransaction(transaction!!)
        clickOn(find<Node>("#refreshTransactionsBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        clickOn(formatter!!.format(transaction.valueDate!!))
        clickOn(find<Node>("#deleteTransactionBtn") as JFXButton)
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertEquals(0, (find<Node>("#transactionsTableView") as TableView<*>).items.size)
        val categoryRoot = LocalStorageImpl.getInstance().readObject<CategoryRoot>("categories")
        Assertions.assertEquals(0, (TreeUtil.getByValue(categoryRoot, transaction.category,
                Comparator.comparingLong(Category::id)) as Category).transactions.size)
    }
}
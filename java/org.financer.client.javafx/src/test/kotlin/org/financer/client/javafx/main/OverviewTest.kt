package org.financer.client.javafx.main

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import org.financer.client.format.I18N.get
import org.financer.client.javafx.components.DoubleField
import org.financer.shared.domain.model.value.objects.ValueDate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.time.LocalDate

@Tag("integration")
class OverviewTest : AbstractFinancerApplicationTest() {
    @BeforeEach
    @Throws(Exception::class)
    public override fun setUpEach() {
        super.setUpEach()
    }

    @Test
    fun testLastTransactions() {
        register(user(), password())
        Assertions.assertNotNull(clickOn(get("noRecentTransactions")))
        val transaction = variableTransaction()
        transaction!!.setValueDate(ValueDate(LocalDate.now().withDayOfMonth(1)))
        addCategory(variableCategory())
        addVariableTransaction(transaction)
        clickOn(find<Node>("#overviewTabBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        val gridPane = find<GridPane>("#lastTransactionsGridPane")
        Assertions.assertEquals(2, gridPane!!.children.size)
    }

    @Test
    fun testUpcomingFixedTransactions() {
        register(user(), password())
        Assertions.assertNotNull(clickOn(get("noUpcomingTransactions")))
        addCategory(fixedCategory())
        addFixedTransaction(fixedTransaction())
        clickOn(find<Node>("#overviewTabBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        var gridPane = find<GridPane>("#upcomingFixedTransactionGridPane")
        // 2 children because of 2 columns in grid pane
        Assertions.assertEquals(2, gridPane!!.children.size)
        clickOn(gridPane.children[0])
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#transactionAmountTextField") as DoubleField)
        eraseText(4).write("500.0")
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        gridPane = find("#upcomingFixedTransactionGridPane")
        // 1 since placeholder label
        Assertions.assertEquals(1, gridPane.children.size)
        Assertions.assertEquals(500.0, categoryTree.getAmount(ValueDate()).amount)
    }
}
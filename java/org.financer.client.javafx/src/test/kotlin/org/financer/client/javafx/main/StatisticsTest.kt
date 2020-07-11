package org.financer.client.javafx.main

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXDatePicker
import javafx.geometry.VerticalDirection
import javafx.scene.Node
import javafx.scene.chart.AreaChart
import javafx.scene.chart.PieChart
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.ScrollPane
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.financer.client.domain.model.category.Category
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integration")
class StatisticsTest : AbstractFinancerApplicationTest() {
    @BeforeEach
    @Throws(Exception::class)
    public override fun setUpEach() {
        super.setUpEach()
    }

    @Test
    fun testProgressChart() {
        val transaction = variableTransaction()
        register(user(), password())
        addCategory(variableCategory())
        addVariableTransaction(transaction!!)
        clickOn(find<Node>("#statisticsTabBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        var chart = find<AreaChart<*, *>>("#progressLineChart")
        Assertions.assertEquals(1, chart.data.size)
        clickOn(find<Node>("#progressChartDefaultCategoryComboBox") as ComboBox<*>)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.ENTER).release(KeyCode.ENTER)
        clickOn(find<Node>("#addCategoryBtn") as JFXButton)
        val categoriesContainer = find<VBox>("#categoriesContainer")
        val dropDownHBox = categoriesContainer!!.children[1] as HBox
        val categoryComboBox = dropDownHBox.children[0] as JFXComboBox<Category>
        clickOn(categoryComboBox)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.ENTER).release(KeyCode.ENTER)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        chart = find("#progressLineChart")
        Assertions.assertEquals(2, chart.data.size)
    }

    @Test
    fun testFixedExpensesDistributionChart() {
        val transaction = fixedTransaction()
        register(user(), password())
        addCategory(fixedCategory())
        addFixedTransaction(transaction!!)
        clickOn(find<Node>("#statisticsTabBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#statisticsScrollPane") as ScrollPane)
        scroll(50, VerticalDirection.DOWN)
        val pieChart = find<PieChart>("#fixedExpensesDistributionChart")
        Assertions.assertEquals(1, pieChart!!.data.size)
        Assertions.assertFalse(find<Node>("#fixedExpensesNoDataLabel").isVisible)
        val fromDatePicker = find<JFXDatePicker>("#fixedExpensesFromDatePicker")
        fromDatePicker!!.value = transaction.timeRange!!.startDate.minusMonths(5)
        val toDatePicker = find<JFXDatePicker>("#fixedExpensesToDatePicker")
        toDatePicker!!.value = transaction.timeRange!!.startDate.minusMonths(4)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertTrue(find<Node>("#fixedExpensesNoDataLabel").isVisible)
    }

    @Test
    fun testVariableExpensesDistributionChart() {
        val transaction = variableTransaction()
        register(user(), password())
        addCategory(variableCategory())
        addVariableTransaction(transaction!!)
        clickOn(find<Node>("#statisticsTabBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#statisticsScrollPane") as ScrollPane)
        scroll(50, VerticalDirection.DOWN)
        val pieChart = find<PieChart>("#variableExpensesDistributionChart")
        Assertions.assertEquals(1, pieChart!!.data.size)
        Assertions.assertFalse(find<Node>("#variableExpensesNoDataLabel").isVisible)
        val fromDatePicker = find<JFXDatePicker>("#variableExpensesFromDatePicker")
        fromDatePicker!!.value = transaction.valueDate!!.date.minusMonths(5)
        val toDatePicker = find<JFXDatePicker>("#variableExpensesToDatePicker")
        toDatePicker!!.value = transaction.valueDate!!.date.minusMonths(4)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertTrue(find<Node>("#variableExpensesNoDataLabel").isVisible)
    }
}
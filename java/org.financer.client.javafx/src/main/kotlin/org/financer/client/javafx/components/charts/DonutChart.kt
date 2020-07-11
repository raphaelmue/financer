package org.financer.client.javafx.components.charts

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.chart.PieChart
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

/**
 * Displays a Donut Chart. The data is displayed just like in a {@see PieChart}, but with a hole in the center.
 */
class DonutChart : PieChart() {
    private val donutWidth: DoubleProperty = SimpleDoubleProperty(100.0)
    private val innerCircle: Circle = Circle()
    override fun layoutChartChildren(top: Double, left: Double, contentWidth: Double, contentHeight: Double) {
        super.layoutChartChildren(top, left, contentWidth, contentHeight)
        addInnerCircleIfNotPresent()
        updateInnerCircleLayout()
        innerCircle.toFront()
    }

    /**
     * Set the width of the donut.
     *
     * @param donutWidth width of the donut
     */
    fun setDonutWidth(donutWidth: Double) {
        this.donutWidth.set(donutWidth)
    }

    /**
     * Get the width of the donut.
     *
     * @return width of the donut.
     */
    fun getDonutWidth(): Double {
        return donutWidth.get()
    }

    private fun addInnerCircleIfNotPresent() {
        if (!data.isEmpty()) {
            val pie = data[0].node
            if (pie.parent is Pane) {
                val parent = pie.parent as Pane
                if (!parent.children.contains(innerCircle)) {
                    parent.children.add(innerCircle)
                }
            }
        }
    }

    private fun updateInnerCircleLayout() {
        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var maxY = Double.MIN_VALUE
        for (data in data) {
            val node = data.node
            val bounds = node.boundsInParent
            if (bounds.minX < minX) {
                minX = bounds.minX
            }
            if (bounds.minY < minY) {
                minY = bounds.minY
            }
            if (bounds.maxX > maxX) {
                maxX = bounds.maxX
            }
            if (bounds.maxY > maxY) {
                maxY = bounds.maxY
            }
        }
        innerCircle.centerX = minX + (maxX - minX) / 2
        innerCircle.centerY = minY + (maxY - minY) / 2
        innerCircle.radius = (maxX - minX - donutWidth.doubleValue()) / 2
    }

    init {

        // just styled in code for demo purposes,
        // use a style class instead to style via css.
        innerCircle.fill = Color.WHITE
        dataProperty().addListener { _: ObservableValue<out ObservableList<Data?>?>?, _: ObservableList<Data?>?, _: ObservableList<Data?>? ->
            addInnerCircleIfNotPresent()
            updateInnerCircleLayout()
        }
    }
}
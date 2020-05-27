/*
 * Copyright (c) 2017 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.financer.client.javafx.components.charts

import javafx.animation.FadeTransition
import javafx.animation.PauseTransition
import javafx.animation.SequentialTransition
import javafx.beans.NamedArg
import javafx.beans.Observable
import javafx.beans.property.*
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.chart.AreaChart
import javafx.scene.chart.Axis
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.*
import javafx.util.Duration
import java.util.*
import java.util.function.Consumer

/**
 * User: hansolo Date: 03.11.17 Time: 04:50
 */
class SmoothedChart<X, Y>(@NamedArg("xAxis") xAxis: Axis<X>?, @NamedArg("yAxis") yAxis: Axis<Y>?) : AreaChart<X, Y>(xAxis, yAxis) {
    enum class ChartType {
        AREA, LINE
    }

    private var _smoothed = false
    private var smoothed: BooleanProperty? = null
    private var _chartType: ChartType? = null
    private val chartType: ObjectProperty<ChartType>? = null
    private var _subDivisions = 0
    private val subDivisions: IntegerProperty? = null
    private var _snapToTicks = false
    private val snapToTicks: BooleanProperty? = null
    private val _symbolsVisible = false
    private val symbolsVisible: BooleanProperty? = null
    private var _selectorFillColor: Color? = null
    private val selectorFillColor: ObjectProperty<Color>? = null
    private var _selectorStrokeColor: Color? = null
    private val selectorStrokeColor: ObjectProperty<Color>? = null
    private var _selectorSize = 0.0
    private val selectorSize: DoubleProperty? = null
    private var _decimals = 0
    private val decimals: IntegerProperty? = null
    private var formatString: String? = null
    private var selector: Circle? = null
    private var selectorTooltip: Tooltip? = null
    private var chartPlotBackground: Region? = null
    private var timeBeforeFadeOut: PauseTransition? = null
    private var fadeInFadeOut: SequentialTransition? = null
    private var strokePaths: MutableList<Path>? = null
    private var _interactive = false
    private val interactive: BooleanProperty? = null
    private var _tooltipTimeout = 0.0
    private val tooltipTimeout: DoubleProperty? = null
    private var horizontalGridLines: Path? = null
        get() {
            if (null == field) {
                for (node in lookupAll(".chart-horizontal-grid-lines")) {
                    if (node is Path) {
                        field = node
                        break
                    }
                }
            }
            return field
        }
    private var verticalGridLines: Path? = null
        get() {
            if (null == field) {
                for (node in lookupAll(".chart-vertical-grid-lines")) {
                    if (node is Path) {
                        field = node
                        break
                    }
                }
            }
            return field
        }
    private val horizontalZeroLine: Line? = null
    private val verticalZeroLine: Line? = null
    private var clickHandler: EventHandler<MouseEvent>? = null
    private var seriesListener: ListChangeListener<Series<X, Y>>? = null

    // ******************** Initialization ************************************
    private fun init() {
        styleClass.add("smoothed-chart")
        _smoothed = true
        _chartType = ChartType.LINE
        _subDivisions = 16
        _snapToTicks = false
        _selectorFillColor = Color.WHITE
        _selectorStrokeColor = Color.RED
        _selectorSize = 10.0
        _decimals = 2
        _interactive = false
        _tooltipTimeout = 2000.0
        formatString = "%.2f"
        strokePaths = ArrayList()
        clickHandler = EventHandler { evt: MouseEvent -> select(evt) }
        val endOfTransformationHandler = EventHandler { e: ActionEvent? -> selectorTooltip!!.hide() }
        seriesListener = ListChangeListener { change: ListChangeListener.Change<out Series<X, Y>> ->
            while (change.next()) {
                if (change.wasAdded()) {
                    change.addedSubList.forEach { addedItem: Series<X, Y> ->
                        val strokePath = (addedItem.node as Group).children[1] as Path
                        val fillPath = (addedItem.node as Group).children[0] as Path
                        fillPath.addEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler)
                        strokePath.addEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler)
                        (strokePaths as ArrayList<Path>).add(strokePath)
                    }
                } else if (change.wasRemoved()) {
                    change.removed.forEach { removedItem: Series<X, Y> ->
                        val strokePath = (removedItem.node as Group).children[1] as Path
                        val fillPath = (removedItem.node as Group).children[0] as Path
                        fillPath.removeEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler)
                        strokePath.removeEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler)
                        (strokePaths as ArrayList<Path>).remove(strokePath)
                    }
                }
            }
        }

        // Add selector to chart
        selector = Circle()
        selector!!.fill = _selectorFillColor
        selector!!.stroke = _selectorStrokeColor
        selector!!.opacity = 0.0
        selectorTooltip = Tooltip("")
        Tooltip.install(selector, selectorTooltip)
        val fadeIn = FadeTransition(Duration.millis(100.0), selector)
        fadeIn.fromValue = 0.0
        fadeIn.toValue = 1.0
        timeBeforeFadeOut = PauseTransition(Duration.millis(_tooltipTimeout))
        val fadeOut = FadeTransition(Duration.millis(100.0), selector)
        fadeOut.fromValue = 1.0
        fadeOut.toValue = 0.0
        fadeInFadeOut = SequentialTransition(fadeIn, timeBeforeFadeOut, fadeOut)
        fadeInFadeOut!!.onFinished = endOfTransformationHandler
        chartPlotBackground = getChartPlotBackground()
        chartPlotBackground!!.widthProperty().addListener { o: Observable? -> resizeSelector() }
        chartPlotBackground!!.heightProperty().addListener { o: Observable? -> resizeSelector() }
        chartPlotBackground!!.layoutYProperty().addListener { o: Observable? -> resizeSelector() }
        val horizontalGridLines = horizontalGridLines
        if (null != horizontalGridLines) {
            horizontalGridLines.isMouseTransparent = true
        }
        val verticalGridLines = verticalGridLines
        if (null != verticalGridLines) {
            verticalGridLines.isMouseTransparent = true
        }
        chartChildren.addAll(selector)
    }

    private fun registerListeners() {
        data.addListener(seriesListener)
    }

    // ******************** Public Methods ************************************
    private fun isSmoothed(): Boolean {
        return if (null == smoothed) _smoothed else smoothed!!.get()
    }

    fun setSmoothed(SMOOTHED: Boolean) {
        if (null == smoothed) {
            _smoothed = SMOOTHED
            layoutPlotChildren()
        } else {
            smoothed!!.set(SMOOTHED)
        }
    }

    fun smoothedProperty(): BooleanProperty {
        if (null == smoothed) {
            smoothed = object : BooleanPropertyBase(_smoothed) {
                override fun invalidated() {
                    layoutPlotChildren()
                }

                override fun getBean(): Any {
                    return this@SmoothedChart
                }

                override fun getName(): String {
                    return "smoothed"
                }
            }
        }
        return smoothed!!
    }

    private fun getChartType(): ChartType? {
        return if (null == chartType) _chartType else chartType.get()
    }

    fun setChartType(type: ChartType) {
        if (null == chartType) {
            _chartType = type
            layoutPlotChildren()
        } else {
            chartType.set(type)
        }
    }

    private fun getSubDivisions(): Int {
        return subDivisions?.get() ?: _subDivisions
    }

    private fun isSnapToTicks(): Boolean {
        return snapToTicks?.get() ?: _snapToTicks
    }

    private fun getSelectorSize(): Double {
        return selectorSize?.get() ?: _selectorSize
    }

    private fun isInteractive(): Boolean {
        return interactive?.get() ?: _interactive
    }

    private fun getChartPlotBackground(): Region? {
        if (null == chartPlotBackground) {
            for (node in lookupAll(".chart-plot-background")) {
                if (node is Region) {
                    chartPlotBackground = node
                    break
                }
            }
        }
        return chartPlotBackground
    }

    // ******************** Internal Methods **********************************
    override fun getUserAgentStylesheet(): String {
        return ""
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        val height = layoutBounds.height
        data.forEach(Consumer { series: Series<X?, Y?> ->
            val paths = getPaths(series) ?: return@Consumer
            if (isSmoothed()) {
                smooth(paths[1].elements, paths[0].elements, height)
            }
            paths[0].isVisible = ChartType.AREA == getChartType()
            paths[0].isManaged = ChartType.AREA == getChartType()
        })
    }

    /**
     * Returns an array of paths where the first entry represents the fill path and the second entry represents the
     * stroke path
     *
     * @param series
     * @return an array of paths where [0] == FillPath and [1] == StrokePath
     */
    private fun getPaths(series: Series<X?, Y?>): Array<Path>? {
        if (!data.contains(series)) {
            return null
        }
        val seriesNode = series.node ?: return null
        val seriesGroup = seriesNode as Group
        return if (seriesGroup.children.isEmpty() || seriesGroup.children.size < 2) {
            null
        } else arrayOf( /* FillPath   */seriesGroup.children[0] as Path,  /* StrokePath */
                seriesGroup.children[1] as Path)
    }

    private fun resizeSelector() {
        selectorTooltip!!.hide()
        selector!!.isVisible = false
        selector!!.radius = getSelectorSize() * 0.5
        selector!!.strokeWidth = getSelectorSize() * 0.25
    }

    private fun select(evt: MouseEvent) {
        if (!isInteractive()) {
            return
        }
        val EVENT_X = evt.x
        val EVENT_Y = evt.y
        val CHART_X = chartPlotBackground!!.boundsInParent.minX
        val CHART_MIN_Y = chartPlotBackground!!.boundsInParent.minY
        val CHART_HEIGHT = chartPlotBackground!!.boundsInParent.height
        if (yAxis !is NumberAxis) {
            return
        }
        val upperBound = (yAxis as NumberAxis).upperBound
        val lowerBound = (yAxis as NumberAxis).lowerBound
        val range = upperBound - lowerBound
        val factor = range / yAxis.layoutBounds.height
        var elements: List<PathElement?>? = null
        var noOfElements = 0
        var pathBounds: Bounds? = null
        var pathMinX = 0.0
        var pathWidth = 0.0
        var lastElement: PathElement? = null
        var series: Series<X?, Y?>? = null
        for (s in data) {
            val paths = getPaths(s)
            val type = getChartType()!!.ordinal // AREA == 0, LINE == 1 in ChartType enum
            assert(paths != null)
            if (paths!![type].contains(EVENT_X, EVENT_Y)) {
                series = s
                elements = paths[type].elements
                noOfElements = elements.size
                lastElement = elements[0]
                pathBounds = paths[1].layoutBounds
                pathMinX = pathBounds.minX
                pathWidth = pathBounds.width
                break
            }
        }
        if (null == series || series.data.isEmpty()) {
            return
        }
        if (isSnapToTicks()) {
            val reverseFactor = CHART_HEIGHT / range
            val noOfDataElements = series.data.size
            val interval = pathWidth / (noOfDataElements - 1).toDouble()
            val selectedIndex = Helper.roundDoubleToInt((EVENT_X - pathMinX) / interval)
            val selectedData = series.data[selectedIndex]
            val selectedYValue = selectedData.yValue
            if (selectedYValue !is Number) {
                return
            }
            val selectedValue: Double = selectedYValue as Double
            selector!!.centerX = pathMinX + CHART_X + interval * selectedIndex
            selector!!.centerY = CHART_MIN_Y + CHART_HEIGHT - selectedValue * reverseFactor
            selector!!.isVisible = true
            fadeInFadeOut!!.playFrom(Duration.millis(0.0))
            val tooltipLocation = selector!!.localToScreen(selector!!.centerX, selector!!.centerY)
            val tooltipText = StringBuilder(selectedData.xValue.toString()).append("\n").append(selectedData.yValue).toString()
            selectorTooltip!!.text = tooltipText
            selectorTooltip!!.x = tooltipLocation.x
            selectorTooltip!!.y = tooltipLocation.y
            selectorTooltip!!.show(scene.window)
            fireEvent(SmoothedChartEvent(this@SmoothedChart, null, SmoothedChartEvent.DATA_SELECTED, selectedValue))
        } else {
            var i = 1
            while (i < noOfElements) {
                val element = elements!![i]
                val xy = getXYFromPathElement(lastElement)
                val xy1 = getXYFromPathElement(element)
                if (xy[0] < 0 || xy[1] < 0 || xy1[0] < 0 || xy1[1] < 0) {
                    i++
                    continue
                }
                if (EVENT_X > xy[0] && EVENT_X < xy1[0]) {
                    val deltaX = xy1[0] - xy[0]
                    val deltaY = xy1[1] - xy[1]
                    val m = deltaY / deltaX
                    val y = m * (evt.x - xy[0]) + xy[1]
                    val selectedValue = (yAxis.layoutBounds.height - y) * factor + lowerBound
                    selector!!.centerX = CHART_X + evt.x
                    selector!!.centerY = CHART_MIN_Y + y
                    selector!!.isVisible = true
                    fadeInFadeOut!!.playFrom(Duration.millis(0.0))
                    val tooltipLocation = selector!!.localToScreen(selector!!.centerX, selector!!.centerY)
                    val tooltipText = String.format(Locale.US, formatString!!, selectedValue)
                    selectorTooltip!!.text = tooltipText
                    selectorTooltip!!.x = tooltipLocation.x
                    selectorTooltip!!.y = tooltipLocation.y
                    selectorTooltip!!.show(scene.window)
                    fireEvent(SmoothedChartEvent(this@SmoothedChart, null, SmoothedChartEvent.Companion.DATA_SELECTED, selectedValue))
                    break
                }
                lastElement = element
                i++
            }
        }
    }

    private fun smooth(strokeElements: ObservableList<PathElement>, fillElements: ObservableList<PathElement>, height: Double) {
        if (fillElements.isEmpty()) return
        // as we do not have direct access to the data, first recreate the list of all the data points we have
        val dataPoints = arrayOfNulls<Point2D>(strokeElements.size)
        for (i in strokeElements.indices) {
            val element = strokeElements[i]
            if (element is MoveTo) {
                dataPoints[i] = Point2D(element.x, element.y)
            } else if (element is LineTo) {
                val line = element
                val x = line.x
                val y = line.y
                dataPoints[i] = Point2D(x, y)
            }
        }
        val firstX = dataPoints[0]!!.x
        val lastX = dataPoints[dataPoints.size - 1]!!.x
        val points = Helper.subdividePoints(dataPoints, getSubDivisions())
        fillElements.clear()
        fillElements.add(MoveTo(firstX, height))
        strokeElements.clear()
        strokeElements.add(MoveTo(points[0]!!.x, points[0]!!.y))
        for (p in points) {
            if (p!!.x.compareTo(firstX) >= 0) {
                fillElements.add(LineTo(p.x, p.y))
                strokeElements.add(LineTo(p.x, p.y))
            }
        }
        fillElements.add(LineTo(lastX, height))
        fillElements.add(LineTo(0.0, height))
        fillElements.add(ClosePath())
    }

    private fun getXYFromPathElement(element: PathElement?): DoubleArray {
        return when (element) {
            is MoveTo -> doubleArrayOf(element.x, element.y)
            is LineTo -> doubleArrayOf(element.x, element.y)
            else -> doubleArrayOf(-1.0, -1.0)
        }
    }

    companion object {
        val TRANSPARENT_BACKGROUND = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
        private const val MAX_SUBDIVISIONS = 64
        private const val MAX_DECIMALS = 10
    }

    // ******************** Constructors **************************************
    init {
        init()
        registerListeners()
    }
}
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
package org.financer.client.javafx.components.charts;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.NamedArg;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * User: hansolo
 * Date: 03.11.17
 * Time: 04:50
 */
@SuppressWarnings("all")
public class SmoothedChart<X, Y> extends AreaChart<X, Y> {
    public static final Background TRANSPARENT_BACKGROUND = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));

    public enum ChartType {AREA, LINE}

    private static final int MAX_SUBDIVISIONS = 64;
    private static final int MAX_DECIMALS = 10;
    private boolean _smoothed;
    private BooleanProperty smoothed;
    private ChartType _chartType;
    private ObjectProperty<ChartType> chartType;
    private int _subDivisions;
    private IntegerProperty subDivisions;
    private boolean _snapToTicks;
    private BooleanProperty snapToTicks;
    private boolean _symbolsVisible;
    private BooleanProperty symbolsVisible;
    private Color _selectorFillColor;
    private ObjectProperty<Color> selectorFillColor;
    private Color _selectorStrokeColor;
    private ObjectProperty<Color> selectorStrokeColor;
    private double _selectorSize;
    private DoubleProperty selectorSize;
    private int _decimals;
    private IntegerProperty decimals;
    private String formatString;
    private Circle selector;
    private Tooltip selectorTooltip;
    private Region chartPlotBackground;
    private PauseTransition timeBeforeFadeOut;
    private SequentialTransition fadeInFadeOut;
    private List<Path> strokePaths;
    private boolean _interactive;
    private BooleanProperty interactive;
    private double _tooltipTimeout;
    private DoubleProperty tooltipTimeout;
    private Path horizontalGridLines;
    private Path verticalGridLines;
    private Line horizontalZeroLine;
    private Line verticalZeroLine;
    private EventHandler<MouseEvent> clickHandler;
    private ListChangeListener<Series<X, Y>> seriesListener;


    // ******************** Constructors **************************************
    public SmoothedChart(final @NamedArg("xAxis") Axis<X> xAxis, final @NamedArg("yAxis") Axis<Y> yAxis) {
        super(xAxis, yAxis);
        init();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        getStyleClass().add("smoothed-chart");

        _smoothed = true;
        _chartType = ChartType.LINE;
        _subDivisions = 16;
        _snapToTicks = false;
        _selectorFillColor = Color.WHITE;
        _selectorStrokeColor = Color.RED;
        _selectorSize = 10;
        _decimals = 2;
        _interactive = false;
        _tooltipTimeout = 2000;
        formatString = "%.2f";
        strokePaths = new ArrayList<>();
        clickHandler = this::select;
        EventHandler<ActionEvent> endOfTransformationHandler = e -> selectorTooltip.hide();
        seriesListener = change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(addedItem -> {
                        final Path strokePath = (Path) ((Group) addedItem.getNode()).getChildren().get(1);
                        final Path fillPath = (Path) ((Group) addedItem.getNode()).getChildren().get(0);
                        fillPath.addEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler);
                        strokePath.addEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler);
                        strokePaths.add(strokePath);
                    });
                } else if (change.wasRemoved()) {
                    change.getRemoved().forEach(removedItem -> {
                        final Path strokePath = (Path) ((Group) removedItem.getNode()).getChildren().get(1);
                        final Path fillPath = (Path) ((Group) removedItem.getNode()).getChildren().get(0);
                        fillPath.removeEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler);
                        strokePath.removeEventHandler(MouseEvent.MOUSE_PRESSED, clickHandler);
                        strokePaths.remove(strokePath);
                    });
                }
            }
        };

        // Add selector to chart
        selector = new Circle();
        selector.setFill(_selectorFillColor);
        selector.setStroke(_selectorStrokeColor);
        selector.setOpacity(0);

        selectorTooltip = new Tooltip("");
        Tooltip.install(selector, selectorTooltip);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(100), selector);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        timeBeforeFadeOut = new PauseTransition(Duration.millis(_tooltipTimeout));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), selector);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeInFadeOut = new SequentialTransition(fadeIn, timeBeforeFadeOut, fadeOut);
        fadeInFadeOut.setOnFinished(endOfTransformationHandler);

        chartPlotBackground = getChartPlotBackground();
        chartPlotBackground.widthProperty().addListener(o -> resizeSelector());
        chartPlotBackground.heightProperty().addListener(o -> resizeSelector());
        chartPlotBackground.layoutYProperty().addListener(o -> resizeSelector());

        Path horizontalGridLines = getHorizontalGridLines();
        if (null != horizontalGridLines) {
            horizontalGridLines.setMouseTransparent(true);
        }

        Path verticalGridLines = getVerticalGridLines();
        if (null != verticalGridLines) {
            verticalGridLines.setMouseTransparent(true);
        }

        getChartChildren().addAll(selector);
    }

    private void registerListeners() {
        getData().addListener(seriesListener);
    }


    // ******************** Public Methods ************************************
    private boolean isSmoothed() {
        return null == smoothed ? _smoothed : smoothed.get();
    }

    public void setSmoothed(final boolean SMOOTHED) {
        if (null == smoothed) {
            _smoothed = SMOOTHED;
            layoutPlotChildren();
        } else {
            smoothed.set(SMOOTHED);
        }
    }

    public BooleanProperty smoothedProperty() {
        if (null == smoothed) {
            smoothed = new BooleanPropertyBase(_smoothed) {
                @Override
                protected void invalidated() {
                    layoutPlotChildren();
                }

                @Override
                public Object getBean() {
                    return SmoothedChart.this;
                }

                @Override
                public String getName() {
                    return "smoothed";
                }
            };
        }
        return smoothed;
    }

    private ChartType getChartType() {
        return null == chartType ? _chartType : chartType.get();
    }

    public void setChartType(final ChartType TYPE) {
        if (null == chartType) {
            _chartType = TYPE;
            layoutPlotChildren();
        } else {
            chartType.set(TYPE);
        }
    }

    private int getSubDivisions() {
        return null == subDivisions ? _subDivisions : subDivisions.get();
    }

    private boolean isSnapToTicks() {
        return null == snapToTicks ? _snapToTicks : snapToTicks.get();
    }

    private double getSelectorSize() {
        return null == selectorSize ? _selectorSize : selectorSize.get();
    }

    private boolean isInteractive() {
        return null == interactive ? _interactive : interactive.get();
    }

    private Region getChartPlotBackground() {
        if (null == chartPlotBackground) {
            for (Node node : lookupAll(".chart-plot-background")) {
                if (node instanceof Region) {
                    chartPlotBackground = (Region) node;
                    break;
                }
            }
        }
        return chartPlotBackground;
    }

    private Path getHorizontalGridLines() {
        if (null == horizontalGridLines) {
            for (Node node : lookupAll(".chart-horizontal-grid-lines")) {
                if (node instanceof Path) {
                    horizontalGridLines = (Path) node;
                    break;
                }
            }
        }
        return horizontalGridLines;
    }

    private Path getVerticalGridLines() {
        if (null == verticalGridLines) {
            for (Node node : lookupAll(".chart-vertical-grid-lines")) {
                if (node instanceof Path) {
                    verticalGridLines = (Path) node;
                    break;
                }
            }
        }
        return verticalGridLines;
    }

    // ******************** Internal Methods **********************************
    @Override
    public String getUserAgentStylesheet() {
        return "";
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();

        double height = getLayoutBounds().getHeight();
        getData().forEach(series -> {
            final Path[] paths = getPaths(series);
            if (null == paths) {
                return;
            }
            if (isSmoothed()) {
                smooth(paths[1].getElements(), paths[0].getElements(), height);
            }
            paths[0].setVisible(ChartType.AREA == getChartType());
            paths[0].setManaged(ChartType.AREA == getChartType());
        });
    }

    /**
     * Returns an array of paths where the first entry represents the fill path
     * and the second entry represents the stroke path
     *
     * @param series
     * @return an array of paths where [0] == FillPath and [1] == StrokePath
     */
    private Path[] getPaths(final Series<X, Y> series) {
        if (!getData().contains(series)) {
            return null;
        }

        Node seriesNode = series.getNode();
        if (null == seriesNode) {
            return null;
        }

        Group seriesGroup = (Group) seriesNode;
        if (seriesGroup.getChildren().isEmpty() || seriesGroup.getChildren().size() < 2) {
            return null;
        }

        return new Path[]{ /* FillPath   */ (Path) (seriesGroup).getChildren().get(0),
                /* StrokePath */ (Path) (seriesGroup).getChildren().get(1)};
    }

    private void resizeSelector() {
        selectorTooltip.hide();
        selector.setVisible(false);
        selector.setRadius(getSelectorSize() * 0.5);
        selector.setStrokeWidth(getSelectorSize() * 0.25);
    }

    private void select(final MouseEvent evt) {
        if (!isInteractive()) {
            return;
        }

        final double EVENT_X = evt.getX();
        final double EVENT_Y = evt.getY();
        final double CHART_X = chartPlotBackground.getBoundsInParent().getMinX();
        final double CHART_MIN_Y = chartPlotBackground.getBoundsInParent().getMinY();
        final double CHART_HEIGHT = chartPlotBackground.getBoundsInParent().getHeight();

        if (!(getYAxis() instanceof NumberAxis)) {
            return;
        }

        double upperBound = ((NumberAxis) getYAxis()).getUpperBound();
        double lowerBound = ((NumberAxis) getYAxis()).getLowerBound();
        double range = upperBound - lowerBound;
        double factor = range / getYAxis().getLayoutBounds().getHeight();
        List<PathElement> elements = null;
        int noOfElements = 0;
        Bounds pathBounds = null;
        double pathMinX = 0;
        double pathWidth = 0;
        PathElement lastElement = null;

        Series<X, Y> series = null;
        for (Series<X, Y> s : getData()) {
            Path[] paths = getPaths(s);
            int type = getChartType().ordinal(); // AREA == 0, LINE == 1 in ChartType enum
            assert paths != null;
            if (paths[type].contains(EVENT_X, EVENT_Y)) {
                series = s;
                elements = paths[type].getElements();
                noOfElements = elements.size();
                lastElement = elements.get(0);
                pathBounds = paths[1].getLayoutBounds();
                pathMinX = pathBounds.getMinX();
                pathWidth = pathBounds.getWidth();
                break;
            }
        }

        if (null == series || series.getData().isEmpty()) {
            return;
        }

        if (isSnapToTicks()) {
            double reverseFactor = CHART_HEIGHT / range;
            int noOfDataElements = series.getData().size();
            double interval = pathWidth / (double) (noOfDataElements - 1);
            int selectedIndex = Helper.roundDoubleToInt((EVENT_X - pathMinX) / interval);
            Data<X, Y> selectedData = series.getData().get(selectedIndex);
            Y selectedYValue = selectedData.getYValue();

            if (!(selectedYValue instanceof Number)) {
                return;
            }
            double selectedValue = ((Number) selectedYValue).doubleValue();

            selector.setCenterX(pathMinX + CHART_X + interval * selectedIndex);
            selector.setCenterY((CHART_MIN_Y + CHART_HEIGHT) - (selectedValue * reverseFactor));
            selector.setVisible(true);
            fadeInFadeOut.playFrom(Duration.millis(0));

            Point2D tooltipLocation = selector.localToScreen(selector.getCenterX(), selector.getCenterY());
            String tooltipText = new StringBuilder(selectedData.getXValue().toString()).append("\n").append(selectedData.getYValue()).toString();
            selectorTooltip.setText(tooltipText);
            selectorTooltip.setX(tooltipLocation.getX());
            selectorTooltip.setY(tooltipLocation.getY());
            selectorTooltip.show(getScene().getWindow());

            fireEvent(new SmoothedChartEvent(SmoothedChart.this, null, SmoothedChartEvent.DATA_SELECTED, selectedValue));
        } else {
            int i = 1;
            while (i < noOfElements) {
                PathElement element = elements.get(i);

                double[] xy = getXYFromPathElement(lastElement);
                double[] xy1 = getXYFromPathElement(element);
                if (xy[0] < 0 || xy[1] < 0 || xy1[0] < 0 || xy1[1] < 0) {
                    i++;
                    continue;
                }

                if (EVENT_X > xy[0] && EVENT_X < xy1[0]) {
                    double deltaX = xy1[0] - xy[0];
                    double deltaY = xy1[1] - xy[1];
                    double m = deltaY / deltaX;
                    double y = m * (evt.getX() - xy[0]) + xy[1];
                    double selectedValue = ((getYAxis().getLayoutBounds().getHeight() - y) * factor + lowerBound);

                    selector.setCenterX(CHART_X + evt.getX());
                    selector.setCenterY(CHART_MIN_Y + y);
                    selector.setVisible(true);
                    fadeInFadeOut.playFrom(Duration.millis(0));

                    Point2D tooltipLocation = selector.localToScreen(selector.getCenterX(), selector.getCenterY());
                    String tooltipText = String.format(Locale.US, formatString, selectedValue);
                    selectorTooltip.setText(tooltipText);
                    selectorTooltip.setX(tooltipLocation.getX());
                    selectorTooltip.setY(tooltipLocation.getY());
                    selectorTooltip.show(getScene().getWindow());

                    fireEvent(new SmoothedChartEvent(SmoothedChart.this, null, SmoothedChartEvent.DATA_SELECTED, selectedValue));
                    break;
                }
                lastElement = element;
                i++;
            }
        }
    }

    private void smooth(ObservableList<PathElement> strokeElements, ObservableList<PathElement> fillElements, final double HEIGHT) {
        if (fillElements.isEmpty()) return;
        // as we do not have direct access to the data, first recreate the list of all the data points we have
        final Point2D[] dataPoints = new Point2D[strokeElements.size()];
        for (int i = 0; i < strokeElements.size(); i++) {
            final PathElement element = strokeElements.get(i);
            if (element instanceof MoveTo) {
                final MoveTo move = (MoveTo) element;
                dataPoints[i] = new Point2D(move.getX(), move.getY());
            } else if (element instanceof LineTo) {
                final LineTo line = (LineTo) element;
                final double x = line.getX();
                final double y = line.getY();
                dataPoints[i] = new Point2D(x, y);
            }
        }
        double firstX = dataPoints[0].getX();
        double lastX = dataPoints[dataPoints.length - 1].getX();

        Point2D[] points = Helper.subdividePoints(dataPoints, getSubDivisions());

        fillElements.clear();
        fillElements.add(new MoveTo(firstX, HEIGHT));

        strokeElements.clear();
        strokeElements.add(new MoveTo(points[0].getX(), points[0].getY()));

        for (Point2D p : points) {
            if (Double.compare(p.getX(), firstX) >= 0) {
                fillElements.add(new LineTo(p.getX(), p.getY()));
                strokeElements.add(new LineTo(p.getX(), p.getY()));
            }
        }

        fillElements.add(new LineTo(lastX, HEIGHT));
        fillElements.add(new LineTo(0, HEIGHT));
        fillElements.add(new ClosePath());
    }

    private double[] getXYFromPathElement(final PathElement element) {
        if (element instanceof MoveTo) {
            return new double[]{((MoveTo) element).getX(), ((MoveTo) element).getY()};
        } else if (element instanceof LineTo) {
            return new double[]{((LineTo) element).getX(), ((LineTo) element).getY()};
        } else {
            return new double[]{-1, -1};
        }
    }
}
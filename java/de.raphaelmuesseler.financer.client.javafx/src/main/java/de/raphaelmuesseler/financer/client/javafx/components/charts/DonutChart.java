package de.raphaelmuesseler.financer.client.javafx.components.charts;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Displays a Donut Chart. The data is displayed just like in a {@see PieChart}, but with a hole in the center.
 */
public class DonutChart extends PieChart {

    private DoubleProperty donutWidth = new SimpleDoubleProperty(100);
    private final Circle innerCircle;

    public DonutChart() {
        super();

        innerCircle = new Circle();

        // just styled in code for demo purposes,
        // use a style class instead to style via css.
        innerCircle.setFill(Color.WHITE);

        this.dataProperty().addListener((observableValue, data, t1) -> {
            addInnerCircleIfNotPresent();
            updateInnerCircleLayout();
        });
    }

    @Override
    protected void layoutChartChildren(double top, double left, double contentWidth, double contentHeight) {
        super.layoutChartChildren(top, left, contentWidth, contentHeight);

        addInnerCircleIfNotPresent();
        updateInnerCircleLayout();
        innerCircle.toFront();
    }


    public Circle getInnerCircle() {
        return innerCircle;
    }

    /**
     * Set the width of the donut.
     *
     * @param donutWidth width of the donut
     */
    public void setDonutWidth(double donutWidth) {
        this.donutWidth.set(donutWidth);
    }

    /**
     * Get the width of the donut.
     *
     * @return width of the donut.
     */
    public double getDonutWidth() {
        return donutWidth.get();
    }

    private void addInnerCircleIfNotPresent() {
        if (!getData().isEmpty()) {
            Node pie = getData().get(0).getNode();
            if (pie.getParent() instanceof Pane) {
                Pane parent = (Pane) pie.getParent();

                if (!parent.getChildren().contains(innerCircle)) {
                    parent.getChildren().add(innerCircle);
                }
            }
        }
    }

    private void updateInnerCircleLayout() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for (PieChart.Data data : getData()) {
            Node node = data.getNode();

            Bounds bounds = node.getBoundsInParent();
            if (bounds.getMinX() < minX) {
                minX = bounds.getMinX();
            }
            if (bounds.getMinY() < minY) {
                minY = bounds.getMinY();
            }
            if (bounds.getMaxX() > maxX) {
                maxX = bounds.getMaxX();
            }
            if (bounds.getMaxY() > maxY) {
                maxY = bounds.getMaxY();
            }
        }

        innerCircle.setCenterX(minX + (maxX - minX) / 2);
        innerCircle.setCenterY(minY + (maxY - minY) / 2);

        innerCircle.setRadius((maxX - minX - this.donutWidth.doubleValue()) / 2);
    }
}
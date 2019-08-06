package de.raphaelmuesseler.financer.client.javafx.components;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class DoubleField extends JFXTextField {
    public DoubleField() {
        super();

        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?([[.],][0-9]*)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        StringConverter<Double> converter = new StringConverter<>() {

            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0;
                } else {
                    return Double.valueOf(s.replace(",", "."));
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };

        this.setTextFormatter(new TextFormatter<>(converter, 0.0, filter));
    }

    public void setValue(double value) {
        this.setText(Double.toString(value));
    }

    public double getValue() {
        return Double.valueOf(this.getText());
    }
}

package org.financer.client.javafx.components;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public final class IntegerField extends JFXTextField {
    public IntegerField() {
        super();

        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        StringConverter<Integer> converter = new StringConverter<>() {

            @Override
            public Integer fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0;
                } else {
                    return Integer.valueOf(s);
                }
            }

            @Override
            public String toString(Integer d) {
                return d.toString();
            }
        };

        this.setTextFormatter(new TextFormatter<>(converter, 0, filter));
    }

    public void setValue(int value) {
        this.setText(Integer.toString(value));
    }

    public int getValue() {
        return Integer.parseInt(this.getText());
    }
}

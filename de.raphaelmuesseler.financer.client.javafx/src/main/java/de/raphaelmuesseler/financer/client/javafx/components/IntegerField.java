package de.raphaelmuesseler.financer.client.javafx.components;

import javafx.scene.control.TextField;

/**
 * This kind of input field only allows integers as input.
 *
 * @author Raphael Müßeler
 */
public final class IntegerField extends TextField {
    private int minValue;
    private int maxValue;

    public IntegerField() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    }

    public IntegerField(int initialValue) {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, initialValue);
    }

    public IntegerField(int minValue, int maxValue) {
        this(minValue, maxValue, 0);
    }

    public IntegerField(int minValue, int maxValue, int initialValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setValue(initialValue);
    }


    @Override
    public final void replaceText(int start, int end, String text) {
        if (validate(text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public final void replaceSelection(String text) {
        if (validate(text)) {
            super.replaceSelection(text);
        }
    }

    /**
     * Sets the value of the IntegerField.
     * @param value integer value
     */
    public void setValue(int value) {
        this.setText(Integer.toString(value));
    }

    /**
     * Returns the current value in the IntegerField.
     * @return integer value
     */
    public int getValue() {
        return Integer.valueOf(this.getText());
    }

    private boolean validate(String text) {
        return text.matches("[0-9]*") && Integer.valueOf(text) >= minValue && Integer.valueOf(text) <= maxValue;
    }

    /**
     * Sets the maximum value that may not be exceeded by the input.
     * @param maxValue maximum value
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Sets the minimum value which must not be undercut by the input.
     * @param minValue minimum value
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }
}

package org.financer.client.javafx.dialogs;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.layout.Region;

public final class FinancerTextInputDialog extends FinancerDialog<String> {

    private JFXTextField textField;
    private final String placeholder;

    public FinancerTextInputDialog() {
        this(null, null);
    }

    public FinancerTextInputDialog(String headerText) {
        this(headerText, null);
    }

    public FinancerTextInputDialog(String headerText, String placeholder) {
        super(null);
        this.placeholder = placeholder;
        this.setDialogTitle(headerText);
        this.prepareDialogContent();
    }

    @Override
    protected Region getDialogContent() {
        this.textField = new JFXTextField();
        this.textField.setId("inputDialogTextField");
        return this.textField;
    }

    @Override
    protected void prepareDialogContent() {
        this.textField.setText(this.placeholder);
    }

    @Override
    protected boolean checkConsistency() {
        return true;
    }

    @Override
    protected void onConfirm() {
        this.setValue(this.textField.getText());
        super.onConfirm();
    }
}

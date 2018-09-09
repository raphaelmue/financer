package de.raphaelmuesseler.financer.client.ui.dialogs;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;

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
        this.setHeaderText(headerText);
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);

        this.prepareDialogContent();
    }

    @Override
    protected Node setDialogContent() {
        this.textField = new JFXTextField();
        return this.textField;
    }

    @Override
    protected void prepareDialogContent() {
        this.textField.setText(this.placeholder);
    }

    @Override
    protected String onConfirm() {
        this.setValue(this.textField.getText());
        return super.onConfirm();
    }
}

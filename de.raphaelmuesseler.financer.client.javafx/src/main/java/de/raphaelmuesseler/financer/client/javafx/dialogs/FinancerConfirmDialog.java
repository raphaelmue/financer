package de.raphaelmuesseler.financer.client.javafx.dialogs;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class FinancerConfirmDialog extends FinancerDialog<Boolean> {
    private Label questionLabel;
    private String question;

    public FinancerConfirmDialog(String question) {
        super(false);
        this.question = question;

        this.prepareDialogContent();
    }

    @Override
    protected Region getDialogContent() {
        this.questionLabel = new Label();
        return this.questionLabel;
    }

    @Override
    protected void prepareDialogContent() {
        this.questionLabel.setText(this.question);
    }

    @Override
    protected boolean checkConsistency() {
        return true;
    }

    @Override
    protected void onConfirm() {
        this.setValue(true);
        super.onConfirm();
    }

    @Override
    protected void onCancel() {
        this.setValue(false);
        super.onCancel();
    }
}

package de.raphaelmuesseler.financer.client.javafx.dialogs;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class FinancerConfirmDialog extends FinancerDialog<Boolean> {
    private Label questionLabel;
    private String question;

    public FinancerConfirmDialog(String question) {
        super(false);
        this.question = question;

        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);

        this.prepareDialogContent();
    }

    @Override
    protected Node setDialogContent() {
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
    protected Boolean onConfirm() {
        return true;
    }

    @Override
    protected Boolean onCancel() {
        return false;
    }
}

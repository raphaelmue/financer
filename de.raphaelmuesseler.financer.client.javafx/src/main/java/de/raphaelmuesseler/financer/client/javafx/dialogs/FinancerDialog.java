package de.raphaelmuesseler.financer.client.javafx.dialogs;

import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public abstract class FinancerDialog<T> extends Dialog<T> {
    private T value;
    private Label errorMessageLabel;
    private String errorMessage;

    public FinancerDialog(T value) {
        super();

        this.value = value;
        this.setResult(this.value);

        this.setTitle("Financer");
        Platform.runLater(() -> ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(
                new Image(LoginApplication.class.getResourceAsStream("/images/icons/financer-icon.png"))));

        VBox vBox = new VBox();
        this.errorMessageLabel = new Label();
        this.errorMessageLabel.setStyle("-fx-text-fill: #ff4a39;" +
                "    -fx-padding: 5 0 15 0;" +
                "    -fx-font-weight: 700;");
        this.errorMessageLabel.setManaged(false);
        vBox.getChildren().add(this.errorMessageLabel);
        vBox.getChildren().add(this.setDialogContent());
        this.getDialogPane().setContent(vBox);
    }

    public T showAndGetResult() {
        Optional<T> result = this.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (this.checkConsistency()) {
                return this.onConfirm();
            } else {
                this.showErrorMessage();
                return this.showAndGetResult();
            }
        } else {
            return this.onCancel();
        }
    }

    private void showErrorMessage() {
        this.errorMessageLabel.setText(this.getErrorMessage());
        this.errorMessageLabel.setManaged(true);
    }

    protected abstract boolean checkConsistency();

    protected abstract Node setDialogContent();

    protected void prepareDialogContent() {}

    protected T getValue() {
        return value;
    }

    private String getErrorMessage() {
        return errorMessage;
    }

    protected void setValue(T value) {
        this.value = value;
    }

    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    protected T onCancel() {
        return null;
    }

    protected T onConfirm() {
        return this.getValue();
    }
}

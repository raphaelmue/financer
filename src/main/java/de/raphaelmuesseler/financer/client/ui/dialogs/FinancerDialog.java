package de.raphaelmuesseler.financer.client.ui.dialogs;

import de.raphaelmuesseler.financer.client.ui.login.LoginApplication;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public abstract class FinancerDialog<T> extends Dialog<T> {
    private T value;

    public FinancerDialog(T value) {
        super();

        this.value = value;
        this.setResult(this.value);

        this.setTitle("Financer");
        Platform.runLater(() -> {
            ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(
                    new Image(LoginApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));
        });

        this.getDialogPane().setContent(this.setDialogContent());
    }

    public T showAndGetResult() {
        Optional<T> result = this.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return this.onConfirm();
        } else {
            return this.onCancel();
        }
    }

    protected abstract Node setDialogContent();

    protected void prepareDialogContent() {}

    protected T getValue() {
        return value;
    }

    protected void setValue(T value) {
        this.value = value;
    }

    protected T onCancel() {
        return null;
    }

    protected T onConfirm() {
        return this.getValue();
    }
}

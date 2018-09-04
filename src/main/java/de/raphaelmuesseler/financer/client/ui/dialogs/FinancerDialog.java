package de.raphaelmuesseler.financer.client.ui.dialogs;

import de.raphaelmuesseler.financer.client.ui.login.LoginApplication;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FinancerDialog extends Alert {
    public FinancerDialog(AlertType alertType) {
        super(alertType);

        this.setTitle("Financer");
        ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(new Image(LoginApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));
    }

    public FinancerDialog(AlertType alertType, String header, String contentText) {
        this(alertType);
        this.setHeaderText(header);
        this.setContentText(contentText);
    }
}
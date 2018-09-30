package de.raphaelmuesseler.financer.client.javafx.dialogs;

import de.raphaelmuesseler.financer.client.javafx.main.FinancerApplication;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FinancerAlert extends Alert {
    public FinancerAlert(AlertType alertType) {
        super(alertType);

        this.setTitle("Financer");
        ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(new Image(FinancerApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));
    }

    public FinancerAlert(AlertType alertType, String header, String contentText) {
        this(alertType);
        this.setHeaderText(header);
        this.setContentText(contentText);
    }
}
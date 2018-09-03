package de.raphaelmuesseler.financer.client.ui;

import com.mysql.cj.exceptions.CJCommunicationsException;
import de.raphaelmuesseler.financer.client.ui.login.LoginApplication;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.UnknownHostException;

public class FinancerExceptionDialog extends Alert {
    public FinancerExceptionDialog(String header, Exception exception) {
        super(AlertType.ERROR);

        ((Stage) this.getDialogPane().getScene().getWindow()).getIcons().add(new Image(LoginApplication.class.getResourceAsStream("/images/financer-icon.png")));

        this.setTitle("Financer");
        this.setHeaderText(header);

        String message = "Something went wrong. Please try again later.";
        try {
            throw exception;
        } catch (UnknownHostException | CJCommunicationsException e) {
            message = "The database is not available at the moment. Please try again later";
        } catch (Exception ignored) {
        } finally {
            this.setContentText(message);
        }
    }
}

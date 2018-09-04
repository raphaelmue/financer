package de.raphaelmuesseler.financer.client.ui.dialogs;

import com.mysql.cj.exceptions.CJCommunicationsException;
import de.raphaelmuesseler.financer.client.ui.login.LoginApplication;
import de.raphaelmuesseler.financer.client.ui.main.FinancerApplication;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.ConnectException;
import java.net.UnknownHostException;

public class FinancerExceptionDialog extends FinancerDialog {
    public FinancerExceptionDialog(String header, Exception exception) {
        super(AlertType.ERROR);

        this.setHeaderText(header);

        String message = "Something went wrong. Please try again later.";
        try {
            throw exception;
        } catch (UnknownHostException | CJCommunicationsException e) {
            message = "The database is not available at the moment. Please try again later";
        } catch (ConnectException connectException) {
            message = "Server is currently not available. Please try again later.";
        } catch (Exception ignored) {
        } finally {
            this.setContentText(message);
        }
    }
}

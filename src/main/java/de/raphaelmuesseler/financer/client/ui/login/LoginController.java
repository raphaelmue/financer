package de.raphaelmuesseler.financer.client.ui.login;

import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.client.ui.main.FinancerApplication;
import de.raphaelmuesseler.financer.shared.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.User;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    public TextField emailTextField;
    public PasswordField passwordField;
    public Label errorLabel;
    public GridPane gridPane;
    public VBox progressIndicatorBox;

    private Logger logger = Logger.getLogger("LoginApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();

    public void handleSignInButtonAction() {
        this.gridPane.setDisable(true);
        this.progressIndicatorBox.setVisible(true);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", this.emailTextField.getText());
        parameters.put("password", this.passwordField.getText());
        logger.log(Level.INFO, "User's credentials will be checked ...");
        this.executor.execute(new ServerRequestHandler("checkCredentials", parameters, new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                if (result.getResult() != null) {
                    logger.log(Level.INFO, "User's credentials are correct.");
                    errorLabel.setVisible(false);

                    // storing user data
                    if (!LocalStorage.writeUser((User) result.getResult())) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Financer");
                        alert.setHeaderText("Login");
                        alert.setContentText("Something went wrong storing your personal information. Please try again later.");
                        alert.showAndWait();
                        return;
                    }

                    Platform.runLater(() -> {
                        // open main application
                        Stage stage = (Stage) gridPane.getScene().getWindow();
                        stage.close();

                        try {
                            new FinancerApplication().start(new Stage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    logger.log(Level.INFO, "User's credentials are incorrect.");
                    errorLabel.setVisible(true);
                }
            }

            @Override
            public void onFailure(Exception exception) {
                logger.log(Level.SEVERE, exception.getMessage(), exception);
                Platform.runLater(() -> {
                    FinancerExceptionDialog dialog = new FinancerExceptionDialog("Login", exception);
                    dialog.showAndWait();
                });
            }

            @Override
            public void onAfter() {
                gridPane.setDisable(false);
                progressIndicatorBox.setVisible(false);
            }
        }));
    }
}

package de.raphaelmuesseler.financer.client.ui.login;

import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    public TextField emailTextField;
    public PasswordField passwordField;
    public CheckBox rememberMeCheckBox;
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
        System.out.println(parameters.get("email"));
        logger.log(Level.INFO, "User's credentials will be checked ...");
        this.executor.execute(new ServerRequestHandler("checkCredentials", parameters, result -> {
            if ((Boolean) result.getResult()) {
                logger.log(Level.INFO, "User's credentials are correct.");
                errorLabel.setVisible(false);
            } else {
                logger.log(Level.INFO, "User's credentials are incorrect.");
                errorLabel.setVisible(true);
            }

            this.gridPane.setDisable(false);
            this.progressIndicatorBox.setVisible(false);
        }));
    }
}

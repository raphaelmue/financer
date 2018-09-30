package de.raphaelmuesseler.financer.client.javafx.login;

import de.raphaelmuesseler.financer.client.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerAlert;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerApplication;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.User;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable {

    public TextField emailTextField;
    public PasswordField passwordField;
    public Label errorLabel;
    public GridPane gridPane;
    public VBox progressIndicatorBox;
    public Menu languageMenu;

    private Logger logger = Logger.getLogger("LoginApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();
    private LocalStorageImpl localStorage = LocalStorageImpl.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (this.localStorage.getSettings() != null) {
            for (MenuItem item : this.languageMenu.getItems()) {
                RadioMenuItem radioMenuItem = (RadioMenuItem) item;
                if (radioMenuItem.getUserData().equals(this.localStorage.getSettings().getLanguage().getLanguage())) {
                    radioMenuItem.setSelected(true);
                    break;
                }
            }
        }
        I18N.setLocalStorage(this.localStorage);
        Formatter.setSettings(this.localStorage.getSettings());
    }

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
                    if (!localStorage.writeUser((User) result.getResult())) {
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

    public void onSelectEnglishLanguage() {
        this.changeLanguage(Locale.ENGLISH);
    }

    public void onSelectGermanLanguage() {
        this.changeLanguage(Locale.GERMAN);
    }

    private void changeLanguage(Locale locale) {
        new FinancerAlert(Alert.AlertType.INFORMATION, I18N.get("language"), I18N.get("warnChangesAfterRestart")).showAndWait();

        localStorage.getSettings().setLanguage(locale);
        localStorage.writeSettings(localStorage.getSettings());
    }
}

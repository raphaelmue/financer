package de.raphaelmuesseler.financer.client.ui.login;

import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.local.Settings;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerAlert;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.client.ui.main.FinancerApplication;
import de.raphaelmuesseler.financer.shared.connection.AsyncConnectionCall;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (LocalStorage.getSettings() != null) {
            for (MenuItem item : this.languageMenu.getItems()) {
                RadioMenuItem radioMenuItem = (RadioMenuItem) item;
                if (radioMenuItem.getUserData().equals(LocalStorage.getSettings().getLanguage().getLanguage())) {
                    radioMenuItem.setSelected(true);
                    break;
                }
            }
        }
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

    public void onSelectEnglishLanguage() {
        this.changeLanguage(Locale.ENGLISH);
    }

    public void onSelectGermanLanguage() {
        this.changeLanguage(Locale.GERMAN);
    }

    private void changeLanguage(Locale locale) {
        new FinancerAlert(Alert.AlertType.INFORMATION, I18N.get("language"), I18N.get("warnChangesAfterRestart")).showAndWait();

        Settings settings = LocalStorage.getSettings();
        if (settings == null) {
            settings = new Settings();
        }
        settings.setLanguage(locale);
        LocalStorage.writeSettings(settings);
    }
}

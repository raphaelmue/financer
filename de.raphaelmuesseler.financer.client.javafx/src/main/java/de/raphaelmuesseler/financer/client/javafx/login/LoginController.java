package de.raphaelmuesseler.financer.client.javafx.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerAlert;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerApplication;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import de.raphaelmuesseler.financer.client.local.LocalSettingsImpl;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable, Application {

    @FXML
    public StackPane rootLayout;
    @FXML
    public JFXTextField loginEmailTextField;
    @FXML
    public JFXPasswordField loginPasswordField;
    @FXML
    public Label loginErrorLabel;
    @FXML
    public GridPane gridPane;
    @FXML
    public VBox progressIndicatorBox;
    @FXML
    public Menu languageMenu;

    private static LoginController instance = null;

    private Logger logger = Logger.getLogger("LoginApplication");
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        LocalSettings settings = (LocalSettings) this.localStorage.readObject("localSettings");
        if (settings == null) {
            settings = new LocalSettingsImpl();
            this.localStorage.writeObject("localSettings", settings);
        }
        I18N.setLocalStorage(this.localStorage);

        ServerRequestHandler.setApplication(this);
        ServerRequestHandler.setLocalStorage(this.localStorage);

        Platform.runLater(() -> this.gridPane.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSignInButtonAction();
            }
        }));

        Platform.runLater(() -> this.gridPane.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSignInButtonAction();
            }
        }));
    }

    public void handleSignInButtonAction() {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("email", this.loginEmailTextField.getText());
        parameters.put("password", this.loginPasswordField.getText());
        logger.log(Level.INFO, "User's credentials will be checked ...");
        FinancerExecutor.getExecutor().execute(new ServerRequestHandler("checkCredentials", parameters, new JavaFXAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                if (result.getResult() != null) {
                    Platform.runLater(() -> loginUser((User) result.getResult()));
                } else {
                    logger.log(Level.INFO, "User's credentials are incorrect.");
                    loginErrorLabel.setVisible(true);
                }
            }

            @Override
            public void onFailure(Exception exception) {
                logger.log(Level.SEVERE, exception.getMessage(), exception);
                Platform.runLater(() -> new FinancerExceptionDialog("Login", exception).showAndWait());
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
    }

    public void handleOpenRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog();
        dialog.setOnConfirm(user -> {
            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("user", user);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler("registerUser", parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    Platform.runLater(() -> loginUser((User) result.getResult()));
                }

                @Override
                public void onFailure(Exception exception) {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    JavaFXAsyncConnectionCall.super.onFailure(exception, () -> handleOpenRegisterDialog());
                }
            }));
        });
    }

    private void loginUser(User user) {
        this.logger.log(Level.INFO, "User's credentials are correct.");
        this.loginErrorLabel.setVisible(false);

        // storing user data
        this.localStorage.writeObject("user", user);

        // fetching data
        RetrievalServiceImpl.getInstance().fetchAllData(user, aVoid -> Platform.runLater(() -> {
            // open main application
            Stage stage = (Stage) this.gridPane.getScene().getWindow();
            stage.close();

            try {
                new FinancerApplication().start(new Stage());
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }));
    }

    @Override
    public void showLoadingBox() {
        this.gridPane.setDisable(true);
        this.progressIndicatorBox.setVisible(true);
    }

    @Override
    public void hideLoadingBox() {
        this.gridPane.setDisable(false);
        this.progressIndicatorBox.setVisible(false);
    }

    @Override
    public void setOffline() {
    }

    @Override
    public void setOnline() {
    }

    @Override
    public void showToast(MessageType messageType, String message) {
        throw new UnsupportedOperationException("Showing a toast is not implemented yet!");
    }

    public StackPane getRootLayout() {
        return rootLayout;
    }
}

package de.raphaelmuesseler.financer.client.javafx.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.util.ApplicationHelper;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import de.raphaelmuesseler.financer.client.local.LocalSettingsImpl;
import de.raphaelmuesseler.financer.shared.exceptions.NotAuthorizedException;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
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
    public VBox progressIndicatorBox;
    @FXML
    public JFXButton loginBtn;
    @FXML
    public JFXButton openRegisterDialogBtn;
    @FXML
    public JFXButton closeBtn;

    private static LoginController instance = null;

    private Logger logger = Logger.getLogger("FinancerApplication");
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();

    private double xOffset = 0;
    private double yOffset = 0;

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

        Platform.runLater(() -> this.rootLayout.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSignInButtonAction();
            }
        }));

        Platform.runLater(() -> this.rootLayout.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSignInButtonAction();
            }
        }));

        this.rootLayout.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        this.rootLayout.setOnMouseDragged(event -> {
            this.rootLayout.getScene().getWindow().setX(event.getScreenX() - xOffset);
            this.rootLayout.getScene().getWindow().setY(event.getScreenY() - yOffset);
        });

        this.loginBtn.setText(this.loginBtn.getText().toUpperCase());
        this.openRegisterDialogBtn.setText(this.openRegisterDialogBtn.getText().toUpperCase());
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.closeBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.TIMES));
        this.closeBtn.setOnAction(event -> Platform.exit());
    }

    public void handleSignInButtonAction() {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("email", this.loginEmailTextField.getText());
        parameters.put("password", this.loginPasswordField.getText());
        logger.log(Level.INFO, "User's credentials will be checked ...");
        FinancerExecutor.getExecutor().execute(new ServerRequestHandler("checkCredentials", parameters, (JavaFXAsyncConnectionCall) result -> {
            if (result.getResult() != null) {
                Platform.runLater(() -> loginUser((User) result.getResult()));
            } else {
                logger.log(Level.INFO, "User's credentials are incorrect.");
                loginErrorLabel.setVisible(true);
            }
        }));
    }


    public void handleOpenRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog();
        dialog.setOnConfirm(user -> {
            Map<String, Serializable> parameters = new HashMap<>();
            parameters.put("user", user);

            FinancerExecutor.getExecutor().execute(new ServerRequestHandler("registerUser", parameters,
                    (JavaFXAsyncConnectionCall) result -> Platform.runLater(() -> loginUser((User) result.getResult()))));
        });
    }

    private void loginUser(User user) {
        this.logger.log(Level.INFO, "User's credentials are correct.");
        this.loginErrorLabel.setVisible(false);

        // storing user data
        this.localStorage.writeObject("user", user);

        ApplicationHelper.restartApplication((Stage) this.rootLayout.getScene().getWindow());
    }

    @Override
    public void showLoadingBox() {
        this.rootLayout.setDisable(true);
        this.progressIndicatorBox.setVisible(true);
    }

    @Override
    public void hideLoadingBox() {
        this.rootLayout.setDisable(false);
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

    @Override
    public void showErrorDialog(Exception exception) {
        Platform.runLater(() -> new FinancerExceptionDialog(this.rootLayout, I18N.get("login"), exception));
    }

    public StackPane getRootLayout() {
        return rootLayout;
    }
}

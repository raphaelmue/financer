package de.raphaelmuesseler.financer.client.ui.login;

import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginApplication extends Application {

    private TextField emailTextField;
    private PasswordField passwordTextField;
    private GridPane gridPane;
    private Logger logger = Logger.getLogger("LoginApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();


    @Override
    public void start(Stage primaryStage) {
        this.logger.log(Level.INFO, "Login Application has started.");
        this.intialize(primaryStage);
    }

    private void intialize(Stage primaryStage) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 250);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        VBox vBox = new VBox(progressIndicator);
        vBox.setAlignment(Pos.CENTER);
        vBox.setVisible(false);

        this.gridPane = new GridPane();
        this.gridPane.setAlignment(Pos.CENTER);
        this.gridPane.setHgap(20);
        this.gridPane.setVgap(10);

        // add labels and textfields
        Label errorLabel = new Label("Your credentials are incorrect!");
        errorLabel.setTextFill(Color.web("#ff0000"));
        errorLabel.setTextAlignment(TextAlignment.CENTER);
        errorLabel.setVisible(false);
        this.gridPane.add(errorLabel, 0, 0, 2, 1);

        Label emailLabel = new Label("E-Mail: ");
        this.gridPane.add(emailLabel, 0, 1);
        this.emailTextField = new TextField();
        this.gridPane.add(this.emailTextField, 1, 1);

        Label passwordLabel = new Label("Password: ");
        this.gridPane.add(passwordLabel, 0, 2);
        this.passwordTextField = new PasswordField();
        this.gridPane.add(this.passwordTextField, 1, 2);

        CheckBox rememberMeCheckBox = new CheckBox("Remember Me");
        this.gridPane.add(rememberMeCheckBox, 1, 3);

        // add button
        Button btn = new Button();
        btn.setText("Login");
        btn.setId("login-btn");
        btn.setOnAction(event -> {
            // setting progress indicator
            vBox.setVisible(true);
            gridPane.setDisable(true);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("email", this.emailTextField.getText());
            parameters.put("password", this.passwordTextField.getText());
            logger.log(Level.INFO, "User's credentials will be checked.");
            executor.execute(new ServerRequestHandler("checkCredentials", parameters, result -> {
                if ((Boolean) result.getResult()) {
                    logger.log(Level.INFO, "User's credentials are correct.");
                    errorLabel.setVisible(false);
                } else {
                    logger.log(Level.INFO, "User's credentials are incorrect.");
                    errorLabel.setVisible(true);
                }
                gridPane.setDisable(false);
                vBox.setVisible(false);
            }));
        });
        this.gridPane.add(btn, 0, 4, 2, 1);

        // setting scene
        root.getChildren().add(this.gridPane);
        root.getChildren().add(vBox);

        // setting title
        primaryStage.setTitle("Financer - Login");

        // setting scene
        primaryStage.setScene(scene);

        // show application
        primaryStage.show();
    }
}

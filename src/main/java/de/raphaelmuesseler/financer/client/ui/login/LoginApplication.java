package de.raphaelmuesseler.financer.client.ui.login;

import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginApplication extends Application implements EventHandler<ActionEvent> {

    private TextField emailTextField;
    private PasswordField passwordTextField;
    private Logger logger = Logger.getLogger("LoginApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();


    @Override
    public void start(Stage primaryStage) {
        this.logger.log(Level.INFO, "Login Application has started.");
        this.intialize(primaryStage);
    }

    private void intialize(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(10);

        // add labels and textfields
        final Label emailLabel = new Label("E-Mail: ");
        gridPane.add(emailLabel, 0, 0);
        this.emailTextField = new TextField();
        gridPane.add(this.emailTextField, 1, 0);

        Label passwordLabel = new Label("Password: ");
        gridPane.add(passwordLabel, 0, 1);
        this.passwordTextField = new PasswordField();
        gridPane.add(this.passwordTextField, 1, 1);

        CheckBox rememberMeCheckBox = new CheckBox("Remember Me");
        gridPane.add(rememberMeCheckBox, 1, 2);

        // add button
        Button btn = new Button();
        btn.setText("Login");
        btn.setId("login-btn");
        btn.setOnAction(this);
        gridPane.add(btn, 0, 3, 2, 1);

        // setting scene
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 250);
        root.getChildren().add(gridPane);

        // setting title
        primaryStage.setTitle("Financer - Login");

        // setting scene
        primaryStage.setScene(scene);

        // show application
        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", this.emailTextField.getText());
        parameters.put("password", this.passwordTextField.getText());
        this.logger.log(Level.INFO, "User's credentials will be checked.");
        this.executor.execute(new ServerRequestHandler("checkCredentials", parameters, result -> {
            if ((Boolean) result.getResult()) {
                logger.log(Level.INFO, "User's credentials are correct.");
            } else {
                logger.log(Level.INFO, "User's credentials are incorrect.");
            }
        }));
    }
}

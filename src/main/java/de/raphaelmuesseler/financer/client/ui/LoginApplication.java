package de.raphaelmuesseler.financer.client.ui;

import de.raphaelmuesseler.financer.client.connection.ServerRequest;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginApplication extends Application {

    private TextField emailTextField;
    private PasswordField passwordTextField;

    @Override
    public void start(Stage primaryStage) {
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

        // add button
        Button btn = new Button();
        btn.setText("Login");
        btn.setId("login-btn");
        btn.setOnAction(event -> {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("email", emailTextField.getText());
            parameters.put("password", passwordTextField.getText());
            try {
                System.out.println(new ServerRequest().make("checkCredentials", parameters).getResult());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        gridPane.add(btn, 0, 2, 2, 1);

        // setting scene
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 250);
        root.getChildren().add(gridPane);

        // setting title
        primaryStage.setTitle("Financer");

        // setting scene
        primaryStage.setScene(scene);

        // show application
        primaryStage.show();
    }
}

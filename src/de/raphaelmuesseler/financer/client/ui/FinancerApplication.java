package de.raphaelmuesseler.financer.client.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FinancerApplication extends Application implements EventHandler<ActionEvent> {

    private Socket socket;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.intialize(primaryStage);
    }

    private void intialize(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(10);

        // add labels and textfields
        Label emailLabel = new Label("E-Mail: ");
        gridPane.add(emailLabel, 0, 0);
        TextField emailTextField = new TextField();
        gridPane.add(emailTextField, 1, 0);

        Label passwordLabel = new Label("Password: ");
        gridPane.add(passwordLabel, 0, 1);
        PasswordField passwordTextField = new PasswordField();
        gridPane.add(passwordTextField, 1, 1);

        // add button
        Button btn = new Button();
        btn.setText("Login");
        btn.setOnAction(this);
        gridPane.add(btn, 0, 2, 2, 1);

        // setting scene
        StackPane root = new StackPane();
        root.getChildren().add(gridPane);
        Scene scene = new Scene(root, 300, 250);

        // setting title
        primaryStage.setTitle("Financer");

        // setting scene
        primaryStage.setScene(scene);

        // show application
        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            this.socket = new Socket("localhost", 3500);
            DataOutputStream output = new DataOutputStream(this.socket.getOutputStream());
            output.writeUTF("Test");

            DataInputStream input = new DataInputStream(this.socket.getInputStream());
            System.out.println(input.readUTF());
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

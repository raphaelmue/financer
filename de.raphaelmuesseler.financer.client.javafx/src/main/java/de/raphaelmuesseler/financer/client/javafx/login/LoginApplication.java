package de.raphaelmuesseler.financer.client.javafx.login;

import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginApplication extends Application {

    private Logger logger = Logger.getLogger("LoginApplication");

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (LocalStorageImpl.getInstance().readObject("user") == null) {
            // setting up language
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Financer", Locale.ENGLISH);

            // loading FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/de/raphaelmuesseler/financer/client/javafx/main/views/login.fxml"), resourceBundle);
            Scene scene = new Scene(root, 500, 575);

            scene.getStylesheets().add(getClass().getResource("../main/style/colors.style.css").toExternalForm());

            primaryStage.getIcons().add(new Image(LoginApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));

            primaryStage.setTitle("Financer - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

            this.logger.log(Level.INFO, "Login Application has started.");
        }
    }
}
package de.raphaelmuesseler.financer.client.javafx.login;

import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.server.main.FinancerApplication;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (LocalStorageImpl.getInstance().getLoggedInUser() != null) {
            // open main application
            try {
                new FinancerApplication().start(new Stage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // setting up language
            Locale locale;
            if (LocalStorageImpl.getInstance().getSettings() != null) {
                locale = LocalStorageImpl.getInstance().getSettings().getLanguage();
            } else {
                locale = Locale.ENGLISH;
            }
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Financer", locale);

            // loading FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"), resourceBundle);
            Scene scene = new Scene(root, 500, 575);

            primaryStage.getIcons().add(new Image(LoginApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));

            primaryStage.setTitle("Financer - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

            this.logger.log(Level.INFO, "Login Application has started.");
        }
    }
}
package de.raphaelmuesseler.financer.client.ui.login;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.main.FinancerApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginApplication extends Application {

    private Logger logger = Logger.getLogger("LoginApplication");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (LocalStorage.getLoggedInUser() != null) {
            // open main application
            try {
                new FinancerApplication().start(new Stage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
    
            Scene scene = new Scene(root, 500, 575);

            primaryStage.getIcons().add(new Image(LoginApplication.class.getResourceAsStream("/images/financer-icon.png")));

            primaryStage.setTitle("Financer - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

            this.logger.log(Level.INFO, "Login Application has started.");
        }
    }
}

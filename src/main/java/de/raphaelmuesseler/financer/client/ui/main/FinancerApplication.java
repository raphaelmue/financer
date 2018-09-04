package de.raphaelmuesseler.financer.client.ui.main;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.login.LoginApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinancerApplication extends Application {

    private Logger logger = Logger.getLogger("FinancerApplication");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (LocalStorage.getLoggedInUser() == null) {
            // open login application
            try {
                new LoginApplication().start(new Stage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Parent root = FXMLLoader.load(getClass().getResource("/views/financer.fxml"));

            Scene scene = new Scene(root, 1200, 600);

            scene.getStylesheets().add(getClass().getResource("navbar.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("footer.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("header.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("colors.style.css").toExternalForm());
            primaryStage.getIcons().add(new Image(FinancerApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));

            primaryStage.setTitle("Financer");
            primaryStage.setScene(scene);
            primaryStage.show();

            this.logger.log(Level.INFO, "Financer Application has started.");
        }
    }
}

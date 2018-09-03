package de.raphaelmuesseler.financer.client.ui.main;

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
        Parent root = FXMLLoader.load(getClass().getResource("/views/financer.fxml"));

        Scene scene = new Scene(root, 800, 600);

        primaryStage.getIcons().add(new Image(LoginApplication.class.getResourceAsStream("/images/financer-icon.png")));

        primaryStage.setTitle("Financer");
        primaryStage.setScene(scene);
        primaryStage.show();

        this.logger.log(Level.INFO, "Financer Application has started.");
    }
}

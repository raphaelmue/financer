package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.connection.ServerRequest;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import de.raphaelmuesseler.financer.client.javafx.util.ApplicationHelper;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinancerApplication extends Application {

    private Logger logger = Logger.getLogger("FinancerApplication");

    public static void main(String[] args) {
        if (args.length > 0 && args[0] != null && args[0].equals("local")) {
            ServerRequest.setHost(true);
        } else {
            ServerRequest.setHost(false);
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (LocalStorageImpl.getInstance().readObject("user") == null) {
            // open de.raphaelmuesseler.financer.client.javafx.login application
            try {
                new LoginApplication().start(new Stage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // setting up language
            I18N.setLocalStorage(LocalStorageImpl.getInstance());
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Financer",
                    ApplicationHelper.getLocale((LocalSettings) LocalStorageImpl.getInstance().readObject("localSettings")));

            Parent root = FXMLLoader.load(getClass().getResource("views/financer.fxml"), resourceBundle);

            Scene scene = new Scene(root, 1350, 700);

            scene.getStylesheets().add(getClass().getResource("style/navbar.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/footer.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/header.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/colors.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/main.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/transactions.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/statistics.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/overview.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/profile.style.css").toExternalForm());
            primaryStage.getIcons().add(new Image(FinancerApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));

            Font.loadFont(getClass().getResource("/fonts/Roboto-Regular.ttf").toExternalForm(), 12);
            Font.loadFont(getClass().getResource("/fonts/Roboto-Medium.ttf").toExternalForm(), 12);

            primaryStage.setTitle("Financer");
            primaryStage.setScene(scene);
            primaryStage.show();

            this.logger.log(Level.INFO, "Financer Application has started.");
        }
    }
}

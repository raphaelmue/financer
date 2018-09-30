package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinancerApplication extends Application {

    private Logger logger = Logger.getLogger("FinancerApplication");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        if (LocalStorageImpl.getInstance().getLoggedInUser() == null) {
            // open de.raphaelmuesseler.financer.client.javafx.login application
            try {
                new LoginApplication().start(new Stage());
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
            I18N.setLocalStorage(LocalStorageImpl.getInstance());
            Formatter.setSettings(LocalStorageImpl.getInstance().getSettings());
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Financer", locale);

            Parent root = FXMLLoader.load(getClass().getResource("views/financer.fxml"), resourceBundle);

            Scene scene = new Scene(root, 1200, 650);

            scene.getStylesheets().add(getClass().getResource("style/navbar.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/footer.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/header.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/colors.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/main.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/transactions.style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("style/overview.style.css").toExternalForm());
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

package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.connection.ServerRequest;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.user.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinancerApplication extends Application {

    private Logger logger = Logger.getLogger("FinancerApplication");
    private BooleanProperty ready = new SimpleBooleanProperty(false);

    public static void main(String[] args) {
        ServerRequest.setHost((args.length > 0 && args[0] != null && args[0].equals("local")));
        launch(args);
    }

    @Override
    public void init() {
        User user = (User) LocalStorageImpl.getInstance().readObject("user");
        if (user != null) {
            notifyPreloader(new Preloader.ProgressNotification(0));
            RetrievalServiceImpl.getInstance().fetchCategories(user, result1 -> {
                Platform.runLater(() -> notifyPreloader(new Preloader.ProgressNotification(1.0 / 3.0)));
                RetrievalServiceImpl.getInstance().fetchTransactions(user, result2 -> {
                    Platform.runLater(() -> notifyPreloader(new Preloader.ProgressNotification(2.0 / 3.0)));
                    RetrievalServiceImpl.getInstance().fetchFixedTransactions(user, result3 -> {
                        Platform.runLater(() -> notifyPreloader(new Preloader.ProgressNotification(1)));
                        ready.setValue(Boolean.TRUE);
                        notifyPreloader(new Preloader.StateChangeNotification(
                                Preloader.StateChangeNotification.Type.BEFORE_START));
                    });
                });
            });
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        User user = (User) LocalStorageImpl.getInstance().readObject("user");
        if (user == null) {
            initLoginStage();
        } else {
            I18N.setLocalStorage(LocalStorageImpl.getInstance());

            ready.addListener((observableValue, oldValue, newValue) -> {
                if (Boolean.TRUE.equals(newValue)) {
                    Platform.runLater(() -> {
                        try {
                            initMainStage();
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    });
                }
            });

            this.logger.log(Level.INFO, "Financer Application has started.");
        }
    }

    private void initLoginStage() throws IOException {
        // setting up language
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Financer", Locale.ENGLISH);

        // loading FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/de/raphaelmuesseler/financer/client/javafx/main/views/login.fxml"), resourceBundle);
        Stage primaryStage = new Stage();
        Scene scene = new Scene(root, 800, 500);

        scene.getStylesheets().add(getClass().getResource("../main/style/colors.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("../main/style/form.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("../main/style/login.style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);

        primaryStage.getIcons().add(new Image(FinancerApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));
        Font.loadFont(getClass().getResource("/fonts/Roboto-Regular.ttf").toExternalForm(), 12);
        Font.loadFont(getClass().getResource("/fonts/Roboto-Medium.ttf").toExternalForm(), 12);

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setTitle("Financer - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initMainStage() throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Financer",
                ((User) LocalStorageImpl.getInstance().readObject("user")).getSettings().getLanguage());

        Parent root = FXMLLoader.load(getClass().getResource("views/financer.fxml"), resourceBundle);

        Stage primaryStage = new Stage();
        Scene scene = new Scene(root, 1350, 700);

        scene.getStylesheets().add(getClass().getResource("style/colors.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("../main/style/form.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("style/navbar.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("style/footer.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("style/header.style.css").toExternalForm());
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
    }
}

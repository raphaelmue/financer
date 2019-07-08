package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.connection.ServerRequest;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
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

    private void retrieveData(User user) {
        new Thread(() -> {
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
        }).start();
    }

    @Override
    public void start(Stage primaryStage) {
        User user = (User) LocalStorageImpl.getInstance().readObject("user");
        if (user == null) {
            // open de.raphaelmuesseler.financer.client.javafx.login application
            try {
                new LoginApplication().start(new Stage());
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            I18N.setLocalStorage(LocalStorageImpl.getInstance());

            retrieveData(user);

            ready.addListener((observableValue, oldValue, newValue) -> {
                if (Boolean.TRUE.equals(newValue)) {
                    Platform.runLater(() -> {
                        try {
                            init(primaryStage);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    });
                }
            });

            this.logger.log(Level.INFO, "Financer Application has started.");
        }
    }

    private void init(Stage primaryStage) throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Financer",
                ((User) LocalStorageImpl.getInstance().readObject("user")).getSettings().getLanguage());

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
    }
}

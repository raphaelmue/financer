package org.financer.client.javafx.main;

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
import org.financer.client.domain.api.RestApi;
import org.financer.client.domain.api.RestApiImpl;
import org.financer.client.domain.model.category.CategoryRoot;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.I18N;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.value.objects.SettingPair;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinancerApplication extends Application {

    private final RestApi restApi = new RestApiImpl();
    private final LocalStorage localStorage = new LocalStorageImpl();
    private final Logger logger = Logger.getLogger("FinancerApplication");
    private BooleanProperty ready = new SimpleBooleanProperty(false);

    @Override
    public void init() {
        User user = LocalStorageImpl.getInstance().readObject("user");
        if (user != null) {
            notifyPreloader(new Preloader.ProgressNotification(0));
            restApi.getUsersCategories(user.getId(), categories ->
                    restApi.getUsersVariableTransactions(user.getId(), 0, variableTransactions ->
                            restApi.getUsersFixedTransactions(user.getId()).execute()).execute()).execute();
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.logger.log(Level.INFO, "Financer Application has started.");

        User user = LocalStorageImpl.getInstance().readObject("user");
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
        }
    }

    private void initLoginStage() throws IOException {
        // setting up language
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Financer", Locale.ENGLISH);

        // loading FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/org/financer/client/javafx/main/views/login.fxml"), resourceBundle);
        Stage primaryStage = new Stage();
        Scene scene = new Scene(root, 800, 500);

        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/colors.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/form.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/login.style.css").toExternalForm());
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
                (Locale) ((User) LocalStorageImpl.getInstance().readObject("user")).getValueOrDefault(SettingPair.Property.LANGUAGE));

        Parent root = FXMLLoader.load(getClass().getResource("views/financer.fxml"), resourceBundle);

        Stage primaryStage = new Stage();
        Scene scene = new Scene(root, 1300, 700);

        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/colors.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/form.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/navbar.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/footer.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/header.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/main.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/transactions.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/statistics.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/overview.style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/financer/client/javafx/main/style/profile.style.css").toExternalForm());
        primaryStage.getIcons().add(new Image(FinancerApplication.class.getResourceAsStream("/images/icons/financer-icon.png")));

        Font.loadFont(getClass().getResource("/fonts/Roboto-Regular.ttf").toExternalForm(), 12);
        Font.loadFont(getClass().getResource("/fonts/Roboto-Medium.ttf").toExternalForm(), 12);

        primaryStage.setTitle("Financer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

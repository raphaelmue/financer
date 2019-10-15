package de.raphaelmuesseler.financer.client.javafx.util;

import de.raphaelmuesseler.financer.client.javafx.main.FinancerApplication;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationHelper {

    private static final Logger logger = Logger.getLogger("FinancerApplication");

    private ApplicationHelper() {
        super();
    }

    public static void restartApplication(Stage stage) {
        stage.close();
        Platform.runLater(() -> {
            FinancerApplication financerApplication = new FinancerApplication();
            try {
                financerApplication.init();
                financerApplication.start(new Stage());
                financerApplication.notifyPreloader(new Preloader.ProgressNotification(0));
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }
}

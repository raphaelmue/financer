package de.raphaelmuesseler.financer.client.javafx.util;

import de.raphaelmuesseler.financer.client.javafx.main.FinancerApplication;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationHelper {
    public static void restartApplication(Stage stage) {
        stage.close();
        Platform.runLater(() -> {
            try {
                new FinancerApplication().start(new Stage());
            } catch (IOException e) {
                Logger.getLogger("FinancerApplication").log(Level.SEVERE, e.getMessage(), e);
            }
        });
    }
}

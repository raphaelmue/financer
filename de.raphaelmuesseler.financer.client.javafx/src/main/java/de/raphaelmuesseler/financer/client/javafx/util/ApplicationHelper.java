package de.raphaelmuesseler.financer.client.javafx.util;

import de.raphaelmuesseler.financer.client.javafx.main.FinancerApplication;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ApplicationHelper {

    private ApplicationHelper() {
        super();
    }

    public static void restartApplication(Stage stage) {
        stage.close();
        Platform.runLater(() -> new FinancerApplication().start(new Stage()));
    }
}

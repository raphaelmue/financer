package de.raphaelmuesseler.financer.client.javafx.util;

import de.raphaelmuesseler.financer.client.javafx.main.FinancerApplication;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class ApplicationHelper {

//    public static Locale getLocale(LocalSettings settings) {
//        Locale locale;
//        if (settings != null) {
//            locale = settings.getLanguage();
//        } else {
//            locale = Locale.ENGLISH;
//        }
//        return locale;
//    }

    public static void restartApplication(Stage stage) {
        stage.close();
        Platform.runLater( () -> {
            try {
                new FinancerApplication().start( new Stage() );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

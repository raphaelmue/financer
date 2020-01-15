package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.connection.ServerRequest;
import javafx.application.Application;

public class ApplicationLauncher {
    public static void main(String[] args) {
        ServerRequest.setHost((args.length > 0 && args[0] != null && args[0].equals("local")));
        System.setProperty("javafx.preloader", "de.raphaelmuesseler.financer.client.javafx.main.SplashScreenLoader");
        Application.launch(FinancerApplication.class, args);
    }
}

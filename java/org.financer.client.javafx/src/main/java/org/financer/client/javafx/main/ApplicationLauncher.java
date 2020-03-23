package org.financer.client.javafx.main;

import javafx.application.Application;
import org.financer.client.connection.ServerRequest;

public class ApplicationLauncher {
    public static void main(String[] args) {
        ServerRequest.setHost((args.length > 0 && args[0] != null && args[0].equals("local")));
        System.setProperty("javafx.preloader", "org.financer.client.javafx.main.SplashScreenLoader");
        Application.launch(FinancerApplication.class, args);
    }
}

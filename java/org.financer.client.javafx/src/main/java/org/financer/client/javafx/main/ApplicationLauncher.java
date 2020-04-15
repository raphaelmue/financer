package org.financer.client.javafx.main;

import javafx.application.Application;

public class ApplicationLauncher {
    public static void main(String[] args) {
        System.setProperty("javafx.preloader", "org.financer.client.javafx.main.SplashScreenLoader");
        Application.launch(FinancerApplication.class, args);
    }
}

package de.raphaelmuesseler.financer.client.javafx.main;

import com.sun.javafx.application.LauncherImpl;

public class ApplicationStarter {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(FinancerApplication.class, SplashScreenLoader.class, args);
    }
}

package de.raphaelmuesseler.financer.client.javafx.main;

import com.sun.javafx.application.LauncherImpl;

public class ApplicationLauncher {
    public static void main(String[] args) {
        LauncherImpl.launchApplication(FinancerApplication.class, SplashScreenLoader.class, args);
    }
}

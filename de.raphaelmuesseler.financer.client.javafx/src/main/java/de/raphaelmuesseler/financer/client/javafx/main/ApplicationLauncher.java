package de.raphaelmuesseler.financer.client.javafx.main;

import com.sun.javafx.application.LauncherImpl;
import de.raphaelmuesseler.financer.client.connection.ServerRequest;

public class ApplicationLauncher {
    public static void main(String[] args) {
        ServerRequest.setHost((args.length > 0 && args[0] != null && args[0].equals("local")));
        LauncherImpl.launchApplication(FinancerApplication.class, SplashScreenLoader.class, args);
    }
}

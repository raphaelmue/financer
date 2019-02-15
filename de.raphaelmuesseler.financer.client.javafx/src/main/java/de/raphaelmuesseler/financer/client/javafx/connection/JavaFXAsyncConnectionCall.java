package de.raphaelmuesseler.financer.client.javafx.connection;

import de.raphaelmuesseler.financer.client.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerController;
import de.raphaelmuesseler.financer.shared.exceptions.NotAuthorizedException;
import javafx.application.Platform;

public interface JavaFXAsyncConnectionCall extends AsyncConnectionCall {

    @Override
    default void onFailure(Exception exception) {
        onFailure(exception, () -> {});
    }

    default void onFailure(Exception exception, Runnable runnable) {
        Platform.runLater(() -> {
            FinancerExceptionDialog dialog = new FinancerExceptionDialog("Financer", exception);
            dialog.showAndWait();
            runnable.run();

            if (exception instanceof NotAuthorizedException) {
                FinancerController.handleLogout();
            }
        });

    }
}

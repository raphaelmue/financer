package de.raphaelmuesseler.financer.client.javafx.connection;

import de.raphaelmuesseler.financer.client.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerExceptionDialog;
import javafx.application.Platform;

public interface JavaFXAsyncConnectionCall extends AsyncConnectionCall {
    @Override
    default void onFailure(Exception exception) {
        onFailure(exception, () -> {});
    }

    default void onFailure(Exception exception, Runnable runnable) {
        Platform.runLater(() -> {
            FinancerExceptionDialog dialog = new FinancerExceptionDialog("Login", exception);
            dialog.showAndWait();
            runnable.run();
        });

    };
}

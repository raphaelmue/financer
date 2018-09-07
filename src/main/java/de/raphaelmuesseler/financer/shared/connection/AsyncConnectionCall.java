package de.raphaelmuesseler.financer.shared.connection;

import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerExceptionDialog;
import javafx.application.Platform;

public interface AsyncConnectionCall extends AsyncCall<ConnectionResult> {
    @Override
    void onSuccess(ConnectionResult result);

    @Override
    default void onFailure(Exception exception) {
        Platform.runLater(() -> {
            FinancerExceptionDialog dialog = new FinancerExceptionDialog("Login", exception);
            dialog.showAndWait();
        });
    }

    default void onBefore() {};
    default void onAfter() {};
}

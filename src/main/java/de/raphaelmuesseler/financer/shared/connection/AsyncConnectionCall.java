package de.raphaelmuesseler.financer.shared.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface AsyncConnectionCall extends AsyncCall<ConnectionResult> {
    @Override
    void onSuccess(ConnectionResult result);

    @Override
    default void onFailure(Throwable throwable) {
        Logger logger = Logger.getLogger("AsyncCall");
        logger.log(Level.SEVERE, throwable.getMessage(), throwable);
    }
}

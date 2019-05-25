package de.raphaelmuesseler.financer.client.app.connection;

import de.raphaelmuesseler.financer.client.connection.AsyncConnectionCall;

public interface AndroidAsyncConnectionCall extends AsyncConnectionCall {
    @Override
    default void onFailure(Exception exception) {
        // TODO implement error handling
    }
}

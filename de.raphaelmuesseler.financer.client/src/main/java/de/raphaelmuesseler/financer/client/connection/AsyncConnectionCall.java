package de.raphaelmuesseler.financer.client.connection;

import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;

public interface AsyncConnectionCall extends AsyncCall<ConnectionResult> {
    @Override
    void onSuccess(ConnectionResult result);

    @Override
    default void onFailure(Exception exception) {}

    default void onBefore() {}

    default void onAfter() {}
}

package org.financer.client.connection;

import org.financer.shared.connection.AsyncCall;
import org.financer.shared.connection.ConnectionResult;

public interface AsyncConnectionCall extends AsyncCall<ConnectionResult> {
    @Override
    void onSuccess(ConnectionResult result);

    @Override
    default void onFailure(Exception exception) {
    }

    default void onBefore() {
    }

    @Override
    default void onAfter() {
    }
}

package org.financer.client.connection;

import org.financer.shared.connection.AsyncCall;

public interface RestCallback<T> extends AsyncCall<T> {

    @Override
    void onSuccess(T result);

    @Override
    default void onFailure(Exception exception) {
    }

    default void onBefore() {
    }

    @Override
    default void onAfter() {
    }

}

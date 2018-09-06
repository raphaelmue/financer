package de.raphaelmuesseler.financer.shared.connection;

public interface AsyncConnectionCall extends AsyncCall<ConnectionResult> {
    @Override
    void onSuccess(ConnectionResult result);

    @Override
    default void onFailure(Exception exception) {}

    default void onBefore() {};
    default void onAfter() {};
}

package de.raphaelmuesseler.financer.shared.connection;

public interface AsyncCall<T> {
    void onSuccess(T result);
    default void onFailure(Exception exception) {}
}

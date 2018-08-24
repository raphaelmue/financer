package de.raphaelmuesseler.financer.server.service;

public interface AsyncCall<T> {
    void onSuccess(T result);
    void onFailure(Throwable throwable);
}

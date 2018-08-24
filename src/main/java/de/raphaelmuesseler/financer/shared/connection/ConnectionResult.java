package de.raphaelmuesseler.financer.shared.connection;

public class ConnectionResult<T> {
    private final T result;

    public ConnectionResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}

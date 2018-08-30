package de.raphaelmuesseler.financer.shared.connection;

public class ConnectionResult<T> {
    private final T result;
    private final String errorMessage;

    public ConnectionResult(T result) {
        this.result = result;
        this.errorMessage = null;
    }

    public ConnectionResult(T result, String errorMessage) {
        this.result = result;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T getResult() {
        return result;
    }
}

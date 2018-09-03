package de.raphaelmuesseler.financer.shared.connection;

import java.io.Serializable;

public class ConnectionResult<T> implements Serializable {
    private static final long serialVersionUID = 5566504159960287868L;
    private final T result;
    private final Exception exception;

    public ConnectionResult(T result) {
        this.result = result;
        this.exception = null;
    }

    public ConnectionResult(T result, Exception exception) {
        this.result = result;
        this.exception = exception;
    }

    public Exception getException() {
        return this.exception;
    }

    public T getResult() {
        return result;
    }
}

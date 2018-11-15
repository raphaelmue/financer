package de.raphaelmuesseler.financer.shared.connection;

import java.io.Serializable;

/**
 * By means of the ConnectionResult class, you can receive an Object T on client side.
 *
 * @param <T> Result Type
 * @author Raphael Müßeler
 */
public class ConnectionResult<T> implements Serializable {
    private static final long serialVersionUID = 5566504159960287868L;
    private final T result;
    private final Exception exception;

    /**
     * Instantiate a ConnectionResult object.
     *
     * @param result Result object, which is returned to the user.
     */
    public ConnectionResult(T result) {
        this.result = result;
        this.exception = null;
    }

    /**
     * Instantiate a ConnectionResult object.
     *
     * @param result    Result object, which is returned to the user.
     * @param exception If a Exception is thrown on server side, it will be passed to the client.
     */
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

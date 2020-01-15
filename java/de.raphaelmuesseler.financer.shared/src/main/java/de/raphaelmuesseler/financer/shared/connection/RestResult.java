package de.raphaelmuesseler.financer.shared.connection;

public class RestResult<T> {

    private RestResultStatus status;
    private String statusMessage;
    private T data;

    public RestResult() {

    }

    public RestResult(RestResultStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public RestResultStatus getStatus() {
        return status;
    }

    public void setStatus(RestResultStatus status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

package de.raphaelmuesseler.financer.shared.exceptions;

public abstract class FinancerException extends Exception {
    private String displayMessage;

    FinancerException(String message) {
        super(message);
    }

    public final String getDisplayMessage() {
        return displayMessage;
    }

    public final void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }
}

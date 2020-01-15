package org.financer.shared.exceptions;

public abstract class FinancerException extends Exception {
    private String key;

    FinancerException(String message) {
        super(message);
    }

    public final String getKey() {
        return key;
    }

    public final void setKey(String key) {
        this.key = key;
    }
}

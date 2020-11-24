package org.financer.shared.domain.model;

public interface Expireable {

    default boolean isExpired() {
        return !isValid();
    }

    boolean isValid();

}

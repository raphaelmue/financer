package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Immutable
public class Amount implements Serializable {
    private static final long serialVersionUID = 8647653287643900256L;

    @Column(name = "amount", nullable = false)
    private final double amount;

    public Amount() {
        this.amount = 0;
    }

    public Amount(int amount) {
        this.amount = amount;
    }

    public Amount(double amount) {
        this.amount = amount;
    }

    public boolean isPositive() {
        return this.amount >= 0;
    }

    public boolean isNegative() {
        return !this.isPositive();
    }

    public Amount add(Amount value) {
        return new Amount(this.amount + value.getAmount());
    }

    public Amount multiply(Amount value) {
        return new Amount(this.amount * value.getAmount());
    }

    /*
     * Getters and Setters
     */

    public double getAmount() {
        return amount;
    }
}

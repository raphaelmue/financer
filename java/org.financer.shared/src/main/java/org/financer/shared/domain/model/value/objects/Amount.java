package org.financer.shared.domain.model.value.objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Amount {

    @Column(name = "amount")
    private final double amount;

    public Amount(double amount) {
        this.amount = amount;
    }

    public Amount() {
        this.amount = 0;
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

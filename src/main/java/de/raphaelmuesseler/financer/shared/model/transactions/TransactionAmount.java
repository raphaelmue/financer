package de.raphaelmuesseler.financer.shared.model.transactions;

import java.io.Serializable;
import java.time.LocalDate;

public class TransactionAmount implements Serializable {
    private static final long serialVersionUID = -6751558797407170754L;
    private final int id;
    private final double amount;
    private final LocalDate valueDate;

    public TransactionAmount(int id, double amount, LocalDate valueDate) {
        this.id = id;
        this.amount = amount;
        this.valueDate = valueDate;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    @Override
    public String toString() {
        return this.getAmount() + " [ " + this.getValueDate() + " ]";
    }
}

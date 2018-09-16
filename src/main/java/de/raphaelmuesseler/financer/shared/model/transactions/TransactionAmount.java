package de.raphaelmuesseler.financer.shared.model.transactions;

import java.io.Serializable;
import java.time.LocalDate;

public class TransactionAmount implements Serializable {
    private static final long serialVersionUID = -6751558797407170754L;
    private int id;
    private double amount;
    private LocalDate valueDate;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    @Override
    public String toString() {
        return this.getAmount() + " [ " + this.getValueDate() + " ]";
    }
}

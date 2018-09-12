package de.raphaelmuesseler.financer.shared.model.transactions;

import java.time.LocalDate;

public class TransactionAmount {
    private final int id;
    private final double amount;
    private final LocalDate value_date;

    public TransactionAmount(int id, double amount, LocalDate value_date) {
        this.id = id;
        this.amount = amount;
        this.value_date = value_date;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getValue_date() {
        return value_date;
    }
}

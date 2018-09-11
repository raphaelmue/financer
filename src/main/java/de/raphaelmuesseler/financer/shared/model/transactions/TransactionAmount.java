package de.raphaelmuesseler.financer.shared.model.transactions;

import java.time.LocalDate;

public class TransactionAmount {
    private final double amount;
    private final LocalDate value_date;

    public TransactionAmount(double amount, LocalDate value_date) {
        this.amount = amount;
        this.value_date = value_date;
    }
}

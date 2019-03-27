package de.raphaelmuesseler.financer.shared.model.db;

import java.time.LocalDate;

public class DatabaseFixedTransactionAmount {
    private int id;
    private DatabaseFixedTransaction fixedTransaction;
    private LocalDate valueDate;
    private double amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DatabaseFixedTransaction getFixedTransaction() {
        return fixedTransaction;
    }

    public void setFixedTransaction(DatabaseFixedTransaction fixedTransaction) {
        this.fixedTransaction = fixedTransaction;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.Category;

import java.util.Date;

abstract class AbstractTransaction {
    private int id;
    private final double amount;
    private final Category category;
    private final String product, purpose;
    private final Date valueDate;

    AbstractTransaction(double amount, Category category, String product, String purpose, Date valueDate) {
        this.amount = amount;
        this.category = category;
        this.product = product;
        this.purpose = purpose;
        this.valueDate = valueDate;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }
}

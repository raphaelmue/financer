package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.Category;

import java.io.Serializable;
import java.util.Date;

abstract class AbstractTransaction implements Serializable {
    private static final long serialVersionUID = -2425120066992174442L;

    private int id;
    private final double amount;
    private final Category category;
    private final String product, purpose;
    private final Date valueDate;

    AbstractTransaction(int id, double amount, Category category, String product, String purpose, Date valueDate) {
        this.id = id;
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

    public Date getValueDate() {
        return valueDate;
    }

    public String getProduct() {
        return product;
    }

    public String getPurpose() {
        return purpose;
    }
}

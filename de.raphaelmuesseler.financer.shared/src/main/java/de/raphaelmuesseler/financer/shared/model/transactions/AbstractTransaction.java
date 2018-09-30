package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.Category;

import java.io.Serializable;
import java.time.LocalDate;

abstract class AbstractTransaction implements Serializable {
    private static final long serialVersionUID = -2425120066992174442L;

    private int id;
    private double amount;
    private Category category;
    private String product, purpose;

    AbstractTransaction(int id, double amount, Category category, String product, String purpose) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.product = product;
        this.purpose = purpose;
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

    public String getProduct() {
        return product;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return this.product + " (" + this.amount + " EUR)[" + this.id + "]";
    }
}
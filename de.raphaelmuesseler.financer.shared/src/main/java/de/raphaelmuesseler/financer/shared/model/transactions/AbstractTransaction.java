package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.AmountProvider;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;

import java.io.Serializable;

abstract class AbstractTransaction implements Serializable, AmountProvider {
    private static final long serialVersionUID = -2425120066992174442L;

    private int id;
    private double amount;
    private CategoryTree categoryTree;
    private String product, purpose;

    AbstractTransaction(int id, double amount, CategoryTree category, String product, String purpose) {
        this.id = id;
        this.amount = amount;
        this.categoryTree = category;
        this.product = product;
        this.purpose = purpose;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public CategoryTree getCategoryTree() {
        return categoryTree;
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

    public void setCategoryTree(CategoryTree categoryTree) {
        this.categoryTree = categoryTree;
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

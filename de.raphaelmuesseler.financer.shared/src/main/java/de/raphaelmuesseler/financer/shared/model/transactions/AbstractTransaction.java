package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.AmountProvider;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTransaction implements Serializable, AmountProvider {
    private static final long serialVersionUID = -2425120066992174442L;

    private int id;
    private double amount;
    private CategoryTree categoryTree;
    private String product, purpose;
    private final List<Attachment> attachments;


    AbstractTransaction(int id, double amount, CategoryTree category, String product, String purpose) {
        this.attachments = new ArrayList<>();
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

    public List<Attachment> getAttachments() {
        return attachments;
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

    @Override
    public int hashCode() {
        if (this.id != -1) {
            return 1861610686 + this.id;
        } else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractTransaction && this.id == ((AbstractTransaction) obj).getId();
    }
}

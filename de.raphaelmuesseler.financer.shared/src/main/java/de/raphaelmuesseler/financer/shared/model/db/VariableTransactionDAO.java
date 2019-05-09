package de.raphaelmuesseler.financer.shared.model.db;

import java.time.LocalDate;
import java.util.Set;

public class VariableTransactionDAO implements DataAccessObject {
    private final static long serialVersionUID = -118658876074097774L;

    private int id;
    private CategoryDAO category;
    private LocalDate valueDate;
    private double amount;
    private String product;
    private String purpose;
    private String shop;
    private Set<? extends TransactionAttachmentDAO> attachments;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoryDAO getCategory() {
        return category;
    }

    public void setCategory(CategoryDAO category) {
        this.category = category;
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

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public Set<? extends TransactionAttachmentDAO> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<? extends TransactionAttachmentDAO> attachments) {
        this.attachments = attachments;
    }
}

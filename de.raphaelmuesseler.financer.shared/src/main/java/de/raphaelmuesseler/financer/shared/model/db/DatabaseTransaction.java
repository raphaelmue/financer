package de.raphaelmuesseler.financer.shared.model.db;

import java.time.LocalDate;

public class DatabaseTransaction {
    private int id;
    private DatabaseUser user;
    private DatabaseCategory category;
    private LocalDate valueDate;
    private double amount;
    private String product;
    private String purpose;
    private String shop;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DatabaseUser getUser() {
        return user;
    }

    public void setUser(DatabaseUser user) {
        this.user = user;
    }

    public DatabaseCategory getCategory() {
        return category;
    }

    public void setCategory(DatabaseCategory category) {
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
}
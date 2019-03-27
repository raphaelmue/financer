package de.raphaelmuesseler.financer.shared.model.db;

import java.time.LocalDate;

public class DatabaseFixedTransaction {
    private int id;
    private DatabaseUser user;
    private DatabaseCategory category;
    private double amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String product;
    private String purpose;
    private boolean isVariable;
    private int day;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public boolean getIsVariable() {
        return isVariable;
    }

    public void setIsVariable(boolean variable) {
        isVariable = variable;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}

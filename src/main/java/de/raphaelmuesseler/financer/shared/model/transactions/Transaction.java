package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.Category;

import java.time.LocalDate;

public class Transaction extends AbstractTransaction {
    private String shop;

    public Transaction(int id, double amount, Category category, String product, String purpose, LocalDate valueDate, String shop) {
        super(id, amount, category, product, purpose, valueDate);
        this.shop = shop;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }
}

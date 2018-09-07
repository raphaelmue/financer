package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.Category;

import java.util.Date;

public class Transaction extends AbstractTransaction {


    private final String shop;

    public Transaction(int id, double amount, Category category, String product, String purpose, Date valueDate, String shop) {
        super(id, amount, category, product, purpose, valueDate);
        this.shop = shop;
    }
}

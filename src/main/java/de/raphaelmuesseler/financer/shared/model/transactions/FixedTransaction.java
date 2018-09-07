package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.Category;

import java.util.Date;

public class FixedTransaction extends AbstractTransaction {
    FixedTransaction(int id, double amount, Category category, String product, String purpose, Date valueDate) {
        super(id, amount, category, product, purpose, valueDate);
    }
}
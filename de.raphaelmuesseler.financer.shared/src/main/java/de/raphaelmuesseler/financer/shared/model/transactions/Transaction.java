package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.util.date.DateUtil;

import java.time.LocalDate;

public class Transaction extends AbstractTransaction {
    private String shop;
    private LocalDate valueDate;


    public Transaction(int id, double amount, CategoryTree category, String product, String purpose, LocalDate valueDate, String shop) {
        super(id, amount, category, product, purpose);
        this.valueDate = valueDate;
        this.shop = shop;
    }

    public String getShop() {
        return shop;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    @Override
    public double getAmount(LocalDate localDate) {
        return (DateUtil.checkIfMonthsAreEqual(localDate, this.valueDate) ? this.getAmount() : 0);
    }

    @Override
    public double getAmount(LocalDate startDate, LocalDate endDate) {
        return (startDate.compareTo(this.valueDate) <= 0 && endDate.compareTo(this.valueDate) >= 0 ? this.getAmount() : 0);
    }
}

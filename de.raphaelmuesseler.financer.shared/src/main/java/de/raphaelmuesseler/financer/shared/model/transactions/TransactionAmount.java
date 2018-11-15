package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.AmountProvider;
import de.raphaelmuesseler.financer.util.date.DateUtil;

import java.io.Serializable;
import java.time.LocalDate;

public class TransactionAmount implements Serializable, AmountProvider {
    private static final long serialVersionUID = -6751558797407170754L;
    private int id;
    private double amount;
    private LocalDate valueDate;

    public TransactionAmount(int id, double amount, LocalDate valueDate) {
        this.id = id;
        this.amount = amount;
        this.valueDate = valueDate;
    }

    public int getId() {
        return id;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public double getAmount(LocalDate localDate) {
        return (DateUtil.checkIfMonthsAreEqual(localDate, this.valueDate) ? this.getAmount() : 0);
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    @Override
    public String toString() {
        return this.getAmount() + " [ " + this.getValueDate() + " ]";
    }
}

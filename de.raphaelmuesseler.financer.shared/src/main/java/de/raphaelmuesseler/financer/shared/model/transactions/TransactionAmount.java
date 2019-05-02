package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.db.FixedTransactionAmountDAO;
import de.raphaelmuesseler.financer.util.date.DateUtil;

import java.io.Serializable;
import java.time.LocalDate;

public class TransactionAmount extends FixedTransactionAmountDAO implements Serializable, AmountProvider {
    private static final long serialVersionUID = -6751558797407170754L;

    public TransactionAmount(int id, double amount, LocalDate valueDate) {
        this.setId(id);
        this.setAmount(amount);
        this.setValueDate(valueDate);
    }

    @Override
    public double getAmount() {
        return super.getAmount();
    }

    @Override
    public double getAmount(LocalDate localDate) {
        return (DateUtil.checkIfMonthsAreEqual(localDate, this.getValueDate()) ? this.getAmount() : 0);
    }

    @Override
    public double getAmount(LocalDate startDate, LocalDate endDate) {
        return (startDate.compareTo(this.getValueDate()) <= 0 && endDate.compareTo(this.getValueDate()) >= 0 ? this.getAmount() : 0);
    }

    @Override
    public String toString() {
        return this.getAmount() + " [ " + this.getValueDate() + " ]";
    }
}

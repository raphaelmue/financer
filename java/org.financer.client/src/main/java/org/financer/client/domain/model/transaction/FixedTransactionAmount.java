package org.financer.client.domain.model.transaction;

import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import java.util.Objects;

public class FixedTransactionAmount implements AmountProvider {

    private long id;
    private FixedTransaction fixedTransaction;
    private ValueDate valueDate;
    private Amount amount;

    @Override
    public Amount getAmount(ValueDate valueDate) {
        if (this.getValueDate().isInSameMonth(valueDate)) {
            return this.getAmount();
        } else {
            return new Amount();
        }
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        if (timeRange.includes(this.valueDate)) {
            return this.getAmount();
        } else {
            return new Amount();
        }
    }

    @Override
    public boolean isFixed() {
        return this.fixedTransaction.isFixed();
    }

    @Override
    public boolean isRevenue() {
        return this.fixedTransaction.isRevenue();
    }

    @Override
    public void adjustAmountSign() {
        if ((this.isRevenue() == this.getAmount().isNegative())) {
            this.setAmount(this.getAmount().adjustSign());
        }
    }

    /*
     * Getters and Setters
     */

    public long getId() {
        return id;
    }

    public FixedTransactionAmount setId(long id) {
        this.id = id;
        return this;
    }

    public FixedTransaction getFixedTransaction() {
        return fixedTransaction;
    }

    public FixedTransactionAmount setFixedTransaction(FixedTransaction fixedTransaction) {
        this.fixedTransaction = fixedTransaction;
        return this;
    }

    public ValueDate getValueDate() {
        return valueDate;
    }

    public FixedTransactionAmount setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    @Override
    public Amount getAmount() {
        return amount;
    }

    public FixedTransactionAmount setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedTransactionAmount that = (FixedTransactionAmount) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FixedTransactionAmount [" +
                "id=" + id +
                ", valueDate=" + valueDate +
                ", amount=" + amount +
                ']';
    }
}

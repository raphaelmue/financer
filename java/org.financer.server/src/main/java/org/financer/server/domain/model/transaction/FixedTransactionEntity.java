package org.financer.server.domain.model.transaction;

import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "fixed_transactions")
public class FixedTransactionEntity extends TransactionEntity {
    private static final long serialVersionUID = 8295185142317654835L;

    @Embedded
    private TimeRange timeRange;

    @Column(name = "is_variable")
    private boolean isVariable;

    @Column(name = "day")
    private int day;

    @OneToMany(mappedBy = "fixedTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FixedTransactionAmountEntity> transactionAmounts;

    /**
     * Indicates whether this transaction is active, i.e. whether the time range of this transaction includes the
     * current date.
     *
     * @return true if the transaction is active, false otherwise
     */
    public boolean isActive() {
        return this.timeRange.includes();
    }

    @Override
    public Amount getAmount(ValueDate valueDate) {
        Amount result = new Amount();
        if (this.timeRange.includes(valueDate)) {
            if (this.isVariable) {
                if (this.transactionAmounts != null) {
                    for (FixedTransactionAmountEntity transactionAmount : this.transactionAmounts) {
                        result = result.add(transactionAmount.getAmount(valueDate));
                    }
                }
            } else {
                result = this.getAmount();
            }
        }

        return result;
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        Amount result = new Amount();

        if (this.isVariable) {
            for (FixedTransactionAmountEntity transactionAmount : this.transactionAmounts) {
                result = result.add(transactionAmount.getAmount(timeRange));
            }
        } else {
            result = this.getAmount();
            result = result.multiply(new Amount(this.timeRange.getMonthIntersection(timeRange).getMonthDifference()));
        }

        return result;
    }

    @Override
    public void adjustAmountSign() {
        if (this.isVariable) {
            for (FixedTransactionAmountEntity transactionAmount : this.getTransactionAmounts()) {
                if ((this.getCategory().getCategoryClass().isRevenue() && transactionAmount.getAmount().isNegative()) ||
                        (!this.getCategory().getCategoryClass().isRevenue() && transactionAmount.getAmount().isPositive())) {
                    transactionAmount.setAmount(new Amount(transactionAmount.getAmount().getAmount() * (-1)));
                }
            }
        } else {
            super.adjustAmountSign();
        }
    }

    /*
     * Getters and Setters
     */

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public FixedTransactionEntity setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    public boolean isVariable() {
        return isVariable;
    }

    public FixedTransactionEntity setVariable(boolean variable) {
        isVariable = variable;
        return this;
    }

    public int getDay() {
        return day;
    }

    public FixedTransactionEntity setDay(int day) {
        this.day = day;
        return this;
    }

    public Set<FixedTransactionAmountEntity> getTransactionAmounts() {
        return transactionAmounts;
    }

    public FixedTransactionEntity setTransactionAmounts(Set<FixedTransactionAmountEntity> transactionAmounts) {
        this.transactionAmounts = transactionAmounts;
        return this;
    }
}

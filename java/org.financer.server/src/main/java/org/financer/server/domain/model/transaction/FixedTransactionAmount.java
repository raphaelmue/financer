package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.persistence.*;

@Entity
@Table(name = "fixed_transactions_amounts")
public class FixedTransactionAmount implements DataEntity, AmountProvider {
    private static final long serialVersionUID = -3901962625430867317L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(targetEntity = FixedTransaction.class, fetch = FetchType.EAGER, optional = false)
    private FixedTransaction fixedTransaction;

    @Embedded
    private ValueDate valueDate;

    @Embedded
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

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    protected FixedTransaction getFixedTransaction() {
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
}

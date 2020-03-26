package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.financer.shared.domain.model.AmountProvider;

import javax.persistence.*;

@Entity
@Table(name = "fixed_transactions_amounts")
public class FixedTransactionAmountEntity implements DataEntity, AmountProvider {
    private static final long serialVersionUID = -3901962625430867317L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(targetEntity = FixedTransactionEntity.class, fetch = FetchType.EAGER, optional = false)
    private FixedTransactionEntity fixedTransaction;

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

    protected FixedTransactionEntity getFixedTransaction() {
        return fixedTransaction;
    }

    public FixedTransactionAmountEntity setFixedTransaction(FixedTransactionEntity fixedTransaction) {
        this.fixedTransaction = fixedTransaction;
        return this;
    }

    public ValueDate getValueDate() {
        return valueDate;
    }

    public FixedTransactionAmountEntity setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    @Override
    public Amount getAmount() {
        return amount;
    }

    public FixedTransactionAmountEntity setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }
}

package org.financer.server.domain.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.user.UserProperty;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "fixed_transactions_amounts")
public class FixedTransactionAmount implements DataEntity, AmountProvider, UserProperty {
    private static final long serialVersionUID = -3901962625430867317L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(targetEntity = FixedTransaction.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fixed_transaction_id")
    @ToString.Exclude
    private FixedTransaction fixedTransaction;

    @Embedded
    private ValueDate valueDate;

    @Embedded
    private Amount amount;

    @Override
    public Amount getTotalAmount() {
        return amount;
    }

    @Override
    public Amount getTotalAmount(ValueDate valueDate) {
        if (this.getValueDate().isInSameMonth(valueDate)) {
            return amount;
        } else {
            return new Amount();
        }
    }

    @Override
    public Amount getTotalAmount(TimeRange timeRange) {
        if (timeRange.includes(this.valueDate)) {
            return this.getTotalAmount();
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
        if ((this.isRevenue() == this.getTotalAmount().isNegative())) {
            this.setAmount(this.getTotalAmount().adjustSign());
        }
    }

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.fixedTransaction.isPropertyOfUser(userId);
    }
}

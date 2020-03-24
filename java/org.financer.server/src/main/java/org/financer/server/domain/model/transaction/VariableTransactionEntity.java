package org.financer.server.domain.model.transaction;

import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "transactions")
public class VariableTransactionEntity extends TransactionEntity {
    private static final long serialVersionUID = -118658876074097774L;

    @Override
    public Amount getAmount(ValueDate valueDate) {
        if (this.getValueDate().isInSameMonth(valueDate)) {
            return this.getAmount();
        } else {
            return new Amount(0);
        }
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        if (timeRange.includes(this.getValueDate())) {
            return this.getAmount();
        } else {
            return new Amount(0);
        }
    }
}

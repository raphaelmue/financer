package org.financer.shared.domain.model.api.transaction.fixed;

import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class FixedTransactionAmountDTO {

    @NotNull
    @Min(1)
    private int id;

    @NotNull
    private ValueDate valueDate;

    @NotNull
    private Amount amount;

    public int getId() {
        return id;
    }

    public FixedTransactionAmountDTO setId(int id) {
        this.id = id;
        return this;
    }

    public ValueDate getValueDate() {
        return valueDate;
    }

    public FixedTransactionAmountDTO setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public Amount getAmount() {
        return amount;
    }

    public FixedTransactionAmountDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }
}

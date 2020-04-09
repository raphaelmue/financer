package org.financer.shared.domain.model.api.transaction.fixed;

import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TransactionAmountDTO {

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

    public TransactionAmountDTO setId(int id) {
        this.id = id;
        return this;
    }

    public ValueDate getValueDate() {
        return valueDate;
    }

    public TransactionAmountDTO setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public Amount getAmount() {
        return amount;
    }

    public TransactionAmountDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }
}

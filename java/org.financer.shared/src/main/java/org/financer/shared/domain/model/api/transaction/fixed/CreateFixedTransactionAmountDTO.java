package org.financer.shared.domain.model.api.transaction.fixed;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.constraints.NotNull;

public class CreateFixedTransactionAmountDTO implements DataTransferObject {

    @NotNull
    private ValueDate valueDate;

    @NotNull
    private Amount amount;

    public ValueDate getValueDate() {
        return valueDate;
    }

    public CreateFixedTransactionAmountDTO setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public Amount getAmount() {
        return amount;
    }

    public CreateFixedTransactionAmountDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }
}

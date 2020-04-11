package org.financer.shared.domain.model.api.transaction.fixed;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.constraints.NotNull;

public class UpdateFixedTransactionAmountDTO implements DataTransferObject {

    private ValueDate valueDate;

    private Amount amount;

    public ValueDate getValueDate() {
        return valueDate;
    }

    public UpdateFixedTransactionAmountDTO setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public Amount getAmount() {
        return amount;
    }

    public UpdateFixedTransactionAmountDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

}

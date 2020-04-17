package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.constraints.NotNull;

@Schema(name = "CreateFixedTransactionAmount", description = "Schema for creating a new fixed transaction amount")
public class CreateFixedTransactionAmountDTO implements DataTransferObject {

    @NotNull
    @Schema(description = "Value date of the fixed transaction amount", required = true, example = "2020-01-01")
    private ValueDate valueDate;

    @NotNull
    @Schema(description = "Amount of the fixed transaction amount", required = true, example = "50")
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

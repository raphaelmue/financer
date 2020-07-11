package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Schema(name = "FixedTransactionAmount", description = "Schema of the fixed transaction amount")
public class FixedTransactionAmountDTO {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the fixed transaction amount", required = true, example = "1")
    private int id;

    @NotNull
    @Schema(description = "Value date of the fixed transaction amount", required = true, example = "2020-01-01")
    private ValueDate valueDate;

    @NotNull
    @Schema(description = "Amount of the fixed transaction amount", required = true, example = "50")
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

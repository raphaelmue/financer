package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Schema(name = "FixedTransactionAmount", description = "Schema of the fixed transaction amount")
public class FixedTransactionAmountDTO implements Comparable<FixedTransactionAmountDTO> {

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

    @Override
    public int compareTo(FixedTransactionAmountDTO o) {
        return o.getValueDate().compareTo(this.getValueDate());
    }
}

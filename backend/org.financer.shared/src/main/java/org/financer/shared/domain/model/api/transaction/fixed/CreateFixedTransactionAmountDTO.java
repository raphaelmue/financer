package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Schema(name = "CreateFixedTransactionAmount", description = "Schema for creating a new fixed transaction amount")
public class CreateFixedTransactionAmountDTO implements DataTransferObject {

    @NotNull
    @Schema(description = "Value date of the fixed transaction amount", required = true, example = "2020-01-01")
    private ValueDate valueDate;

    @NotNull
    @Schema(description = "Amount of the fixed transaction amount", required = true, example = "50")
    private Amount amount;
}

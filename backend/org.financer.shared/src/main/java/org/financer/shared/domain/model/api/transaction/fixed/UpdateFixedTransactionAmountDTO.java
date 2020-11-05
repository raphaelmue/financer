package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

@Data
@Accessors(chain = true)
@Schema(name = "UpdateFixedTransactionAmount", description = "Schema for update fixed transaction amount")
public class UpdateFixedTransactionAmountDTO implements DataTransferObject {

    @Schema(description = "Value date of the fixed transaction amount", example = "2020-01-01")
    private ValueDate valueDate;

    @Schema(description = "Amount of the fixed transaction amount", example = "50")
    private Amount amount;

}

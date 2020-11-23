package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
@Schema(name = "FixedTransaction", description = "Schema for a fixed transaction")
public class FixedTransactionDTO {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the fixed transaction", required = true, example = "1")
    private int id;

    @NotNull
    @Schema(description = "Amount of the fixed transaction", required = true)
    private Amount amount;

    @NotNull
    @Schema(description = "Time range of the fixed transaction", required = true)
    private TimeRange timeRange;

    @Schema(description = "Product of the fixed transaction", required = true, example = "Product")
    private String product;

    @Schema(description = "Description of the fixed transaction", required = true, example = "This is a description.")
    private String description;

    @Schema(description = "Vendor of the fixed transaction", required = true, example = "Amazon")
    private String vendor;

    @NotNull
    @Schema(description = "Indicates whether this transaction is variable or not", required = true, example = "false")
    private Boolean hasVariableAmounts;

    @Schema(description = "Billing day of the fixed transaction", required = true, example = "Amazon")
    private int day;

    @Schema(description = "List of fixed transaction amounts", required = true, example = "Amazon")
    private List<@Valid FixedTransactionAmountDTO> transactionAmounts;
}

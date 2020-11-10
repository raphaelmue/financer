package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.util.validation.NotNullConditional;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Accessors(chain = true)
@NotNullConditional(
        fieldName = "isVariable",
        fieldValue = "false",
        dependFieldName = "amount")
@Schema(name = "CreateFixedTransaction", description = "Schema for creating a new fixed transaction")
public class CreateFixedTransactionDTO implements DataTransferObject {

    @Min(1)
    @Schema(description = "Identifier of the category that will be assigned to the fixed transaction", required = true)
    private long categoryId;

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
    private Boolean isVariable;

    @Schema(description = "Billing day of the fixed transaction", required = true)
    private int day;

    @Schema(description = "List of fixed transaction amounts", required = true)
    private Set<@Valid CreateFixedTransactionAmountDTO> transactionAmounts;

}

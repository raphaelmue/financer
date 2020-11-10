package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
@Schema(name = "UpdateFixedTransaction", description = "Schema to update fixed transaction")
public class UpdateFixedTransactionDTO implements DataTransferObject {

    @Schema(description = "Identifier of the category that will be assigned to the fixed transaction")
    private long categoryId;

    @Schema(description = "Amount of the fixed transaction")
    private Amount amount;

    @Schema(description = "Time range of the fixed transaction")
    private TimeRange timeRange;

    @Schema(description = "Product of the fixed transaction", example = "Product")
    private String product;

    @Schema(description = "Description of the fixed transaction", example = "This is a description.")
    private String description;

    @Schema(description = "Vendor of the fixed transaction", example = "Amazon")
    private String vendor;

    @Schema(description = "Indicates whether this transaction is variable or not", example = "false")
    private Boolean isVariable;

    @Schema(description = "Billing day of the fixed transaction")
    private int day;

    @Schema(description = "List of fixed transaction amounts")
    private Set<@Valid FixedTransactionAmountDTO> transactionAmounts = new HashSet<>();

}

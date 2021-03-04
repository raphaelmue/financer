package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.transaction.AttachmentDTO;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@Accessors(chain = true)
@Schema(name = "FixedTransaction", description = "Schema for a fixed transaction")
public class FixedTransactionDTO extends RepresentationModel<FixedTransactionDTO> {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the fixed transaction", required = true, example = "1")
    private int id;

    @NotNull
    @Schema(description = "Category object of the variable transaction", required = true)
    private CategoryDTO category;

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

    @Schema(description = "List of attachments of the variable transaction")
    private Set<AttachmentDTO> attachments;

    @Schema(description = "List of fixed transaction amounts", required = true, example = "Amazon")
    private SortedSet<@Valid FixedTransactionAmountDTO> transactionAmounts = new TreeSet<>();

    @Schema(description = "Indicates wether this transaction is active")
    private boolean isActive;
}

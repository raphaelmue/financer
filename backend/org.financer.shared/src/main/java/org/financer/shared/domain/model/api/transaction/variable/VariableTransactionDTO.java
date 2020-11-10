package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.AmountProviderDTO;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.transaction.AttachmentDTO;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Accessors(chain = true)
@Schema(name = "VariableTransaction", description = "Schema of a variable transaction")
public class VariableTransactionDTO extends RepresentationModel<VariableTransactionDTO> implements AmountProviderDTO {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the variable transaction", required = true, example = "1")
    @EqualsAndHashCode.Include
    private int id;

    @NotNull
    @Schema(description = "Category object of the variable transaction", required = true)
    private CategoryDTO category;

    @NotNull
    @Schema(description = "Value date of the variable transaction", required = true, example = "2020-01-01")
    private ValueDate valueDate;

    @Schema(description = "Description of the variable transaction", example = "This is a description.")
    private String description;

    @Schema(description = "Vendor of the variable transaction", example = "Amazon")
    private String vendor;

    @Schema(description = "List of attachments of the variable transaction")
    private Set<AttachmentDTO> attachments;

    @Schema(description = "List of products of the variable transaction")
    private Set<@Valid ProductDTO> products;

    private Amount totalAmount;

    @Override
    public void setTotalAmount(Amount totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public Amount getTotalAmount() {
        return totalAmount;
    }
}

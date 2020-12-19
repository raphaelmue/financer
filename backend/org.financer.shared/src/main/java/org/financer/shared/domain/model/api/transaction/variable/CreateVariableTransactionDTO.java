package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.api.transaction.CreateAttachmentDTO;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
@Schema(name = "CreateVariableTransaction", description = "Schema for creating a new variable transaction")
public class CreateVariableTransactionDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    @Schema(description = "Category ID of the variable transaction", required = true)
    private long categoryId;

    @NotNull
    @Schema(description = "Value date of the variable transaction", required = true, example = "2020-01-01")
    private ValueDate valueDate;

    @Schema(description = "Description of the variable transaction", example = "This is a description.")
    private String description;

    @Schema(description = "Vendor of the variable transaction", example = "Amazon")
    private String vendor;

    @Schema(description = "List of attachments of the variable transaction")
    private Set<@Valid CreateAttachmentDTO> attachments = new HashSet<>();

    @Schema(description = "List of products of the variable transaction")
    private Set<@Valid CreateProductDTO> products;
}

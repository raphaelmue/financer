package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.ValueDate;

@Data
@Accessors(chain = true)
@Schema(name = "UpdateVariableTransaction", description = "Schema for updating a variable transaction")
public class UpdateVariableTransactionDTO implements DataTransferObject {

    @Schema(description = "Category ID of the variable transaction", required = true)
    private long categoryId;

    @Schema(description = "Value date of the variable transaction", required = true, example = "2020-01-01")
    private ValueDate valueDate;

    @Schema(description = "Description of the variable transaction", example = "This is a description.")
    private String description;

    @Schema(description = "Vendor of the variable transaction", example = "Amazon")
    private String vendor;

}

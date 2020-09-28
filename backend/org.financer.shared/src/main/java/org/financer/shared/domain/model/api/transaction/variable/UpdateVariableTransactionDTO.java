package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.ValueDate;

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

    public long getCategoryId() {
        return categoryId;
    }

    public UpdateVariableTransactionDTO setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public ValueDate getValueDate() {
        return valueDate;
    }

    public UpdateVariableTransactionDTO setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UpdateVariableTransactionDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public UpdateVariableTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }
}

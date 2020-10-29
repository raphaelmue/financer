package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.api.transaction.CreateAttachmentDTO;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

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
    private Set<@Valid CreateAttachmentDTO> attachments;

    @Schema(description = "List of products of the variable transaction")
    private Set<@Valid CreateProductDTO> products;

    public long getCategoryId() {
        return categoryId;
    }

    public CreateVariableTransactionDTO setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public ValueDate getValueDate() {
        return valueDate;
    }

    public CreateVariableTransactionDTO setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CreateVariableTransactionDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public CreateVariableTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public Set<CreateAttachmentDTO> getAttachments() {
        return attachments;
    }

    public CreateVariableTransactionDTO setAttachments(Set<CreateAttachmentDTO> attachments) {
        this.attachments = attachments;
        return this;
    }

    public Set<CreateProductDTO> getProducts() {
        return products;
    }

    public CreateVariableTransactionDTO setProducts(Set<CreateProductDTO> products) {
        this.products = products;
        return this;
    }
}

package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.transaction.AttachmentDTO;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Schema(name = "VariableTransaction", description = "Schema of a variable transaction")
public class VariableTransactionDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the variable transaction", required = true, example = "1")
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

    public int getId() {
        return id;
    }

    public VariableTransactionDTO setId(int id) {
        this.id = id;
        return this;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public VariableTransactionDTO setCategory(CategoryDTO category) {
        this.category = category;
        return this;
    }

    public ValueDate getValueDate() {
        return valueDate;
    }

    public VariableTransactionDTO setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VariableTransactionDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public VariableTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public Set<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public VariableTransactionDTO setAttachments(Set<AttachmentDTO> attachments) {
        this.attachments = attachments;
        return this;
    }

    public Set<ProductDTO> getProducts() {
        return products;
    }

    public VariableTransactionDTO setProducts(Set<ProductDTO> products) {
        this.products = products;
        return this;
    }
}
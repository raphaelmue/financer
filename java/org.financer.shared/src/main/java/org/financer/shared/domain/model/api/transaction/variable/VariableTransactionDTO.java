package org.financer.shared.domain.model.api.transaction.variable;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.transaction.AttachmentDTO;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class VariableTransactionDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    private int id;

    @NotNull
    private CategoryDTO category;

    @NotNull
    private ValueDate valueDate;

    private String description;

    private String vendor;

    private Set<AttachmentDTO> attachments;

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

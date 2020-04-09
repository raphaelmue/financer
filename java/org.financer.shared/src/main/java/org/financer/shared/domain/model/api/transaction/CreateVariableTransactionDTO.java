package org.financer.shared.domain.model.api.transaction;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CreateVariableTransactionDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    private long categoryId;

    @NotNull
    private ValueDate valueDate;

    private String purpose;

    private String vendor;

    private List<@Valid CreateProductDTO> products;

    private List<AttachmentDTO> attachments;

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

    public String getPurpose() {
        return purpose;
    }

    public CreateVariableTransactionDTO setPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public CreateVariableTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public CreateVariableTransactionDTO setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
        return this;
    }

    public List<CreateProductDTO> getProducts() {
        return products;
    }

    public CreateVariableTransactionDTO setProducts(List<CreateProductDTO> products) {
        this.products = products;
        return this;
    }
}

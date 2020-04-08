package org.financer.shared.domain.model.api.transaction;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CreateVariableTransactionDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    private long categoryId;

    @NotNull
    private ValueDate valueDate;

    @NotNull
    private Amount amount;

    private String product;

    private String purpose;

    private String vendor;

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

    public Amount getAmount() {
        return amount;
    }

    public CreateVariableTransactionDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public CreateVariableTransactionDTO setProduct(String product) {
        this.product = product;
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
}

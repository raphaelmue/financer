package org.financer.shared.domain.model.api.transaction;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.api.category.CategoryDTO;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class VariableTransactionDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    private int id;

    private CategoryDTO category;

    private LocalDate valueDate;

    private double amount;

    private int product;

    private int purpose;

    private int shop;

    private List<AttachmentDTO> attachments;

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

    public LocalDate getValueDate() {
        return valueDate;
    }

    public VariableTransactionDTO setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public VariableTransactionDTO setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public int getProduct() {
        return product;
    }

    public VariableTransactionDTO setProduct(int product) {
        this.product = product;
        return this;
    }

    public int getPurpose() {
        return purpose;
    }

    public VariableTransactionDTO setPurpose(int purpose) {
        this.purpose = purpose;
        return this;
    }

    public int getShop() {
        return shop;
    }

    public VariableTransactionDTO setShop(int shop) {
        this.shop = shop;
        return this;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public VariableTransactionDTO setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
        return this;
    }
}

package org.financer.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.financer.shared.model.db.DataEntity;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Validated
public class VariableTransactionDTO implements DataTransferObject {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @JsonProperty("valueDate")
    @ApiModelProperty(value = "Value Date", required = true, example = "2020-02-02")
    private LocalDate valueDate;

    @JsonProperty("amount")
    @ApiModelProperty(value = "Amount", required = true, example = "74.99")
    private double amount;

    @JsonProperty("product")
    @ApiModelProperty(value = "Product", example = "Food")
    private int product;

    @JsonProperty("purpose")
    @ApiModelProperty(value = "Purpose", example = "Meal")
    private int purpose;

    @JsonProperty("shop")
    @ApiModelProperty(value = "Shop", example = "Discounter")
    private int shop;

    @JsonProperty("attachments")
    @ApiModelProperty(value = "List of Attachemnts", example = "123")
    private List<AttachmentDTO> attachments;

    public int getId() {
        return id;
    }

    public VariableTransactionDTO setId(int id) {
        this.id = id;
        return this;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public VariableTransactionDTO setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public VariableTransactionDTO amount(double amount) {
        this.setAmount(amount);
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

    @Override
    public DataEntity toEntity() {
        return null;
    }
}

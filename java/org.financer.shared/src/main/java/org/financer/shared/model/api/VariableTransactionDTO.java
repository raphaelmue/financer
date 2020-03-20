package org.financer.shared.model.api;

import com.google.gson.annotations.SerializedName;
import org.financer.shared.model.db.DataEntity;

import java.time.LocalDate;
import java.util.List;

// @Validated
public class VariableTransactionDTO implements DataTransferObject {

    @SerializedName("id")
    // @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @SerializedName("valueDate")
    // @ApiModelProperty(value = "Value Date", required = true, example = "2020-02-02")
    private LocalDate valueDate;

    @SerializedName("amount")
    // @ApiModelProperty(value = "Amount", required = true, example = "74.99")
    private double amount;

    @SerializedName("product")
    // @ApiModelProperty(value = "Product", example = "Food")
    private int product;

    @SerializedName("purpose")
    // @ApiModelProperty(value = "Purpose", example = "Meal")
    private int purpose;

    @SerializedName("shop")
    // @ApiModelProperty(value = "Shop", example = "Discounter")
    private int shop;

    @SerializedName("attachments")
    // @ApiModelProperty(value = "List of Attachemnts", example = "123")
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

package org.financer.shared.domain.model.api;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.List;

// @Validated
public class FixedTransactionDTO {

    @SerializedName("id")
    // @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @SerializedName("amount")
    // @ApiModelProperty(value = "Identifier", example = "74.99")
    private double amount;

    @SerializedName("startDate")
    // @ApiModelProperty(value = "Start Date", required = true, example = "2020-02-02")
    private LocalDate startDate;

    @SerializedName("endDate")
    // @ApiModelProperty(value = "End Date", example = "2020-02-02")
    private LocalDate endDate;

    @SerializedName("product")
    // @ApiModelProperty(value = "Product", example = "Food")
    private int product;

    @SerializedName("purpose")
    // @ApiModelProperty(value = "Purpose", example = "Meal")
    private int purpose;

    @SerializedName("isVariable")
    // @ApiModelProperty(value = "Indicates whether transaction is variable or not", required = true, example = "false")
    private boolean isVariable;

    @SerializedName("day")
    // @ApiModelProperty(value = "Day of reckoning", example = "1")
    private int day;

    @SerializedName("transactionAmounts")
    // @ApiModelProperty(value = "List of Transaction Amounts")
    private List<TransactionAmountDTO> transactionAmounts;

    public int getId() {
        return id;
    }

    public FixedTransactionDTO setId(int id) {
        this.id = id;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public FixedTransactionDTO setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public FixedTransactionDTO setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public FixedTransactionDTO setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public int getProduct() {
        return product;
    }

    public FixedTransactionDTO setProduct(int product) {
        this.product = product;
        return this;
    }

    public int getPurpose() {
        return purpose;
    }

    public FixedTransactionDTO setPurpose(int purpose) {
        this.purpose = purpose;
        return this;
    }

    public boolean isVariable() {
        return isVariable;
    }

    public FixedTransactionDTO setVariable(boolean variable) {
        isVariable = variable;
        return this;
    }

    public int getDay() {
        return day;
    }

    public FixedTransactionDTO setDay(int day) {
        this.day = day;
        return this;
    }

    public List<TransactionAmountDTO> getTransactionAmounts() {
        return transactionAmounts;
    }

    public FixedTransactionDTO setTransactionAmounts(List<TransactionAmountDTO> transactionAmounts) {
        this.transactionAmounts = transactionAmounts;
        return this;
    }
}

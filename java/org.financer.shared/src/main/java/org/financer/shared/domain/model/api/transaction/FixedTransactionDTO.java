package org.financer.shared.domain.model.api.transaction;

import org.financer.shared.domain.model.value.objects.TimeRange;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class FixedTransactionDTO {

    @NotNull
    @Min(1)
    private int id;

    @NotNull
    private double amount;

    @NotNull
    private TimeRange timeRange;

    private String product;

    private String purpose;

    private String vendor;

    @NotNull
    private boolean isVariable;

    private int day;

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

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public FixedTransactionDTO setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public FixedTransactionDTO setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getPurpose() {
        return purpose;
    }

    public FixedTransactionDTO setPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public FixedTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public boolean getIsVariable() {
        return isVariable;
    }

    public FixedTransactionDTO setIsVariable(boolean variable) {
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

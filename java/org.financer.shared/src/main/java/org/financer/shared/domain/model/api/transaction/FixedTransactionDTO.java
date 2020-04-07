package org.financer.shared.domain.model.api.transaction;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class FixedTransactionDTO {

    @NotNull
    @Min(1)
    private int id;

    private double amount;

    private LocalDate startDate;

    private LocalDate endDate;

    private int product;

    private int purpose;

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

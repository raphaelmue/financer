package org.financer.shared.domain.model.api.transaction;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.util.validation.NotNullConditional;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@NotNullConditional(
        fieldName = "isVariable",
        fieldValue = "false",
        dependFieldName = "amount")
public class CreateFixedTransactionDTO implements DataTransferObject {

    private Amount amount;

    @NotNull
    private long categoryId;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private String product;

    private String purpose;

    private String vendor;

    @NotNull
    private boolean isVariable;

    private int day;

    private List<TransactionAmountDTO> transactionAmounts;

    public Amount getAmount() {
        return amount;
    }

    public CreateFixedTransactionDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public CreateFixedTransactionDTO setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public CreateFixedTransactionDTO setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public CreateFixedTransactionDTO setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public CreateFixedTransactionDTO setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getPurpose() {
        return purpose;
    }

    public CreateFixedTransactionDTO setPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public CreateFixedTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public boolean getIsVariable() {
        return isVariable;
    }

    public CreateFixedTransactionDTO setIsVariable(boolean variable) {
        isVariable = variable;
        return this;
    }

    public int getDay() {
        return day;
    }

    public CreateFixedTransactionDTO setDay(int day) {
        this.day = day;
        return this;
    }

    public List<TransactionAmountDTO> getTransactionAmounts() {
        return transactionAmounts;
    }

    public CreateFixedTransactionDTO setTransactionAmounts(List<TransactionAmountDTO> transactionAmounts) {
        this.transactionAmounts = transactionAmounts;
        return this;
    }
}

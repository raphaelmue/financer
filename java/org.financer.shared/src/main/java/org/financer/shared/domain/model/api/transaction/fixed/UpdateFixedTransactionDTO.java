package org.financer.shared.domain.model.api.transaction.fixed;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;

import javax.validation.Valid;
import java.util.Set;

public class UpdateFixedTransactionDTO implements DataTransferObject {

    private long categoryId;

    private Amount amount;

    private TimeRange timeRange;

    private String product;

    private String description;

    private String vendor;

    private boolean isVariable;

    private int day;

    private Set<@Valid FixedTransactionAmountDTO> transactionAmounts;

    public long getCategoryId() {
        return categoryId;
    }

    public UpdateFixedTransactionDTO setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public Amount getAmount() {
        return amount;
    }

    public UpdateFixedTransactionDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public UpdateFixedTransactionDTO setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public UpdateFixedTransactionDTO setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UpdateFixedTransactionDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public UpdateFixedTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public boolean getIsVariable() {
        return isVariable;
    }

    public UpdateFixedTransactionDTO setIsVariable(boolean variable) {
        isVariable = variable;
        return this;
    }

    public int getDay() {
        return day;
    }

    public UpdateFixedTransactionDTO setDay(int day) {
        this.day = day;
        return this;
    }

    public Set<FixedTransactionAmountDTO> getTransactionAmounts() {
        return transactionAmounts;
    }

    public UpdateFixedTransactionDTO setTransactionAmounts(Set<FixedTransactionAmountDTO> transactionAmounts) {
        this.transactionAmounts = transactionAmounts;
        return this;
    }
}

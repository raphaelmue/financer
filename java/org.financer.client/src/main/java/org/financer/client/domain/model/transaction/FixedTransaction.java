package org.financer.client.domain.model.transaction;

import org.financer.client.domain.model.category.Category;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public final class FixedTransaction extends Transaction {
    private static final long serialVersionUID = 8295185142317654835L;

    private Amount amount;
    private TimeRange timeRange;
    private boolean isVariable;
    private int day;
    private String product;
    private Set<FixedTransactionAmount> transactionAmounts = new HashSet<>();

    /**
     * Indicates whether this transaction is active, i.e. whether the time range of this transaction includes the
     * current date.
     *
     * @return true if the transaction is active, false otherwise
     */
    public boolean isActive() {
        return this.timeRange.includes();
    }

    /**
     * Cancels the fixed transaction by setting the end date to current date.
     */
    public void cancel() {
        this.cancel(LocalDate.now());
    }

    /**
     * Cancels the fixed transaction by setting the end date to the given date.
     *
     * @param endDate end date to be set
     */
    public void cancel(LocalDate endDate) {
        this.setTimeRange(timeRange.setEndDate(endDate));
    }

    @Override
    public Amount getAmount(ValueDate valueDate) {
        Amount result = new Amount();
        if (this.timeRange.includes(valueDate)) {
            if (this.isVariable) {
                if (this.transactionAmounts != null) {
                    for (FixedTransactionAmount transactionAmount : this.transactionAmounts) {
                        result = result.add(transactionAmount.getAmount(valueDate));
                    }
                }
            } else {
                result = this.getAmount();
            }
        }

        return result;
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        Amount result = new Amount();

        if (this.isVariable) {
            for (FixedTransactionAmount transactionAmount : this.transactionAmounts) {
                result = result.add(transactionAmount.getAmount(timeRange));
            }
        } else {
            result = this.getAmount();
            result = result.multiply(new Amount(this.timeRange.getMonthIntersection(timeRange).getMonthDifference()));
        }

        return result;
    }

    @Override
    public void adjustAmountSign() {
        if (this.isVariable) {
            for (AmountProvider amountProvider : this.getTransactionAmounts()) {
                amountProvider.adjustAmountSign();
            }
        } else {
            if ((this.isRevenue() == this.getAmount().isNegative())) {
                this.setAmount(this.getAmount().adjustSign());
            }
        }
    }

    /*
     * Getters and Setters
     */

    @Override
    public Amount getAmount() {
        return amount;
    }

    public FixedTransaction setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public FixedTransaction setId(long id) {
        super.setId(id);
        return this;
    }

    @Override
    public FixedTransaction setCategory(Category category) {
        super.setCategory(category);
        return this;
    }

    @Override
    public FixedTransaction setDescription(String purpose) {
        super.setDescription(purpose);
        return this;
    }

    @Override
    public FixedTransaction setVendor(String vendor) {
        super.setVendor(vendor);
        return this;
    }

    @Override
    public FixedTransaction setAttachments(Set<Attachment> attachments) {
        super.setAttachments(attachments);
        return this;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public FixedTransaction setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    public boolean getIsVariable() {
        return isVariable;
    }

    public FixedTransaction setIsVariable(boolean isVariable) {
        this.isVariable = isVariable;
        return this;
    }

    public int getDay() {
        return day;
    }

    public FixedTransaction setDay(int day) {
        this.day = day;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public FixedTransaction setProduct(String product) {
        this.product = product;
        return this;
    }

    public Set<FixedTransactionAmount> getTransactionAmounts() {
        return isVariable ? new HashSet<>() : transactionAmounts;
    }

    public FixedTransaction setTransactionAmounts(Set<FixedTransactionAmount> transactionAmounts) {
        this.transactionAmounts = transactionAmounts;
        return this;
    }

    public FixedTransaction addFixedTransactionAmount(FixedTransactionAmount fixedTransactionAmount) {
        this.transactionAmounts.add(fixedTransactionAmount);
        return this;
    }

    public FixedTransaction removeFixedTransactionAmount(FixedTransactionAmount fixedTransactionAmount) {
        this.transactionAmounts.remove(fixedTransactionAmount);
        return this;
    }
}

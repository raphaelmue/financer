package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "fixed_transactions")
public class FixedTransaction extends Transaction {
    private static final long serialVersionUID = 8295185142317654835L;

    @Embedded
    private TimeRange timeRange;

    @Column(name = "is_variable", nullable = false)
    private boolean isVariable;

    @Column(name = "day")
    private int day;

    @OneToMany(mappedBy = "fixedTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
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
            for (FixedTransactionAmount transactionAmount : this.getTransactionAmounts()) {
                if ((this.getCategory().getCategoryClass().isRevenue() && transactionAmount.getAmount().isNegative()) ||
                        (!this.getCategory().getCategoryClass().isRevenue() && transactionAmount.getAmount().isPositive())) {
                    transactionAmount.setAmount(new Amount(transactionAmount.getAmount().getAmount() * (-1)));
                }
            }
        } else {
            super.adjustAmountSign();
        }
    }

    /*
     * Getters and Setters
     */

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
    public FixedTransaction setAmount(Amount amount) {
        super.setAmount(amount);
        return this;
    }

    @Override
    public FixedTransaction setProduct(String product) {
        super.setProduct(product);
        return this;
    }

    @Override
    public FixedTransaction setPurpose(String purpose) {
        super.setPurpose(purpose);
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

    public boolean isVariable() {
        return isVariable;
    }

    public FixedTransaction setVariable(boolean variable) {
        isVariable = variable;
        return this;
    }

    public int getDay() {
        return day;
    }

    public FixedTransaction setDay(int day) {
        this.day = day;
        return this;
    }

    public Set<FixedTransactionAmount> getTransactionAmounts() {
        return transactionAmounts;
    }

    public FixedTransaction setTransactionAmounts(Set<FixedTransactionAmount> transactionAmounts) {
        this.transactionAmounts = transactionAmounts;
        return this;
    }
}

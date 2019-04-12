package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseFixedTransaction;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseTransactionAttachment;
import de.raphaelmuesseler.financer.util.date.DateUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FixedTransaction extends DatabaseFixedTransaction implements Transaction {
    private CategoryTree categoryTree;
    private final Set<Attachment> attachments;
    private final Set<TransactionAmount> transactionAmounts;

    public FixedTransaction(int id, double amount, CategoryTree category, LocalDate startDate, LocalDate endDate, String product, String purpose,
                            boolean isVariable, int day, Set<TransactionAmount> transactionAmounts) {
        this.setId(id);
        this.setAmount(amount);
        this.setCategoryTree(category);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
        this.setProduct(product);
        this.setPurpose(purpose);
        this.setIsVariable(isVariable);
        this.setDay(day);
        this.attachments = new HashSet<>();
        this.transactionAmounts = transactionAmounts;
    }

    @Override
    public double getAmount(LocalDate localDate) {
        double amount = 0;

        if (this.getEndDate() == null || this.getEndDate().compareTo(LocalDate.now()) >= 0) {
            if (this.getIsVariable() && this.getTransactionAmounts() != null) {
                for (AmountProvider amountProvider : this.getTransactionAmounts()) {
                    amount += amountProvider.getAmount(localDate);
                }
            }
            if (!this.getIsVariable()) {
                amount = super.getAmount();
            }
        }

        return amount;
    }

    @Override
    public double getAmount() {
        double amount = 0;

        if (this.getEndDate() == null || this.getEndDate().compareTo(LocalDate.now()) >= 0) {
            if (this.getIsVariable() && this.getTransactionAmounts() != null) {
                for (AmountProvider amountProvider : this.getTransactionAmounts()) {
                    amount += amountProvider.getAmount();
                }
            }
            if (!this.getIsVariable()) {
                amount = super.getAmount();
            }
        }

        return amount;
    }

    @Override
    public double getAmount(LocalDate startDate, LocalDate endDate) {
        double amount = 0;

        if (this.getEndDate() == null || this.getEndDate().compareTo(startDate) >= 0) {
            if (this.getIsVariable() && this.getTransactionAmounts() != null) {
                for (AmountProvider amountProvider : this.getTransactionAmounts()) {
                    amount += amountProvider.getAmount(startDate, endDate);
                }
            } else {
                LocalDate maxStartDate, minEndDate;
                if (this.getEndDate() == null) {
                    minEndDate = endDate;
                } else {
                    if (endDate.compareTo(this.getEndDate()) <= 0) {
                        minEndDate = endDate;
                    } else {
                        minEndDate = this.getEndDate();
                    }
                }

                if (startDate.compareTo(this.getStartDate()) >= 0) {
                    maxStartDate = startDate;
                } else {
                    maxStartDate = this.getStartDate();
                }
                amount = super.getAmount() * DateUtil.getMonthDifference(maxStartDate, minEndDate);
            }
        }

        return amount;
    }

    @Override
    public CategoryTree getCategoryTree() {
        return categoryTree;
    }

    @Override
    public Set<? extends DatabaseTransactionAttachment> getAttachments() {
        return this.attachments;
    }

    @Override
    public void setCategoryTree(CategoryTree categoryTree) {
        this.categoryTree = categoryTree;
    }

    public Set<TransactionAmount> getTransactionAmounts() {
        return transactionAmounts;
    }
}
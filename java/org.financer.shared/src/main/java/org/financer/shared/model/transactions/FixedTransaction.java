package org.financer.shared.model.transactions;

import org.financer.shared.model.categories.Category;
import org.financer.shared.model.categories.CategoryTree;
import org.financer.shared.model.categories.CategoryTreeImpl;
import org.financer.shared.model.db.FixedTransactionAmountEntity;
import org.financer.shared.model.db.FixedTransactionEntity;
import org.financer.shared.model.db.AttachmentEntity;
import org.financer.util.date.DateUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FixedTransaction extends FixedTransactionEntity implements Transaction {
    private static final long serialVersionUID = 5624926843417391780L;

    private CategoryTree categoryTree;
    private final Set<Attachment> attachments;
    private final Set<TransactionAmount> transactionAmounts;

    public FixedTransaction(FixedTransactionEntity fixedTransactionEntity) {
        this(fixedTransactionEntity, new CategoryTreeImpl(new Category(fixedTransactionEntity.getCategory())));
    }

    public FixedTransaction(FixedTransactionEntity fixedTransactionEntity, CategoryTree categoryTree) {
        this(fixedTransactionEntity.getId(),
                fixedTransactionEntity.getAmount(),
                categoryTree,
                fixedTransactionEntity.getStartDate(),
                fixedTransactionEntity.getEndDate(),
                fixedTransactionEntity.getProduct(),
                fixedTransactionEntity.getPurpose(),
                fixedTransactionEntity.getIsVariable(),
                fixedTransactionEntity.getDay(),
                new HashSet<>());
        if (fixedTransactionEntity.getTransactionAmounts() != null) {
            for (FixedTransactionAmountEntity fixedTransactionAmountEntity : fixedTransactionEntity.getTransactionAmounts()) {
                this.transactionAmounts.add(new TransactionAmount(fixedTransactionAmountEntity));
            }
        }
    }

    public FixedTransaction(int id, double amount, CategoryTree category, LocalDate startDate, LocalDate endDate, String product, String purpose,
                            boolean isVariable, int day, Set<TransactionAmount> transactionAmounts) {
        this.setId(id);
        this.setAmount(amount);
        this.setCategory(category.getValue());
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

    public double getAmountValue() {
        return super.getAmount();
    }

    @Override
    public double getAmount() {
        return this.getAmount(LocalDate.now());
    }

    @Override
    public double getAmount(LocalDate localDate) {
        double amount = 0;

        if (this.getEndDate() == null || this.getEndDate().compareTo(localDate) >= 0) {
            if (this.getIsVariable() && this.getTransactionAmounts() != null) {
                for (AmountProvider amountProvider : this.getTransactionAmounts()) {
                    amount += amountProvider.getAmount(localDate);
                }
            }
            if (!this.getIsVariable() && (this.getEndDate() == null && this.getStartDate().compareTo(localDate) <= 0 ||
                    DateUtil.isDateBetween(localDate, this.getStartDate(), this.getEndDate()))) {
                amount = super.getAmount();
            }
        }

        return amount;
    }

    @Override
    public double getAmount(LocalDate startDate, LocalDate endDate) {
        double amount = 0;

        if (this.getEndDate() == null || this.getEndDate().compareTo(startDate) >= 0) {
            LocalDate maxStartDate;
            LocalDate minEndDate;
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
            if (minEndDate.compareTo(maxStartDate) >= 0) {
                if (this.getIsVariable()) {
                    if (this.getTransactionAmounts() != null) {
                        for (AmountProvider amountProvider : this.getTransactionAmounts()) {
                            amount += amountProvider.getAmount(maxStartDate, minEndDate);
                        }
                    }
                } else {
                    amount = super.getAmount() * DateUtil.getMonthDifference(maxStartDate, minEndDate);
                }
            }
        }

        return amount;
    }

    public boolean isActive() {
        return LocalDate.now().compareTo(this.getStartDate()) >= 0 &&
                (this.getEndDate() == null || (LocalDate.now().compareTo(this.getEndDate()) <= 0));
    }

    @Override
    public void adjustAmountSign() {
        if (this.getIsVariable()) {
            for (TransactionAmount transactionAmount : this.getTransactionAmounts()) {
                if ((this.getCategoryTree().getValue().getCategoryClass().isRevenue() && transactionAmount.getAmount() < 0) ||
                        (!this.getCategoryTree().getValue().getCategoryClass().isRevenue() && transactionAmount.getAmount() >= 0)) {
                    transactionAmount.setAmount(transactionAmount.getAmount() * (-1));
                }
            }
        } else {
            if ((this.getCategoryTree().getValue().getCategoryClass().isRevenue() && this.getAmount() < 0) ||
                    (!this.getCategoryTree().getValue().getCategoryClass().isRevenue() && this.getAmount() >= 0)) {
                this.setAmount(this.getAmount() * (-1));
            }
        }
    }


    @Override
    public FixedTransactionEntity toEntity() {
        FixedTransactionEntity fixedTransactionEntity = new FixedTransactionEntity();
        fixedTransactionEntity.setId(this.getId());
        fixedTransactionEntity.setAmount(this.getAmountValue());
        fixedTransactionEntity.setCategory(this.getCategoryTree().getValue());
        fixedTransactionEntity.setStartDate(this.getStartDate());
        fixedTransactionEntity.setEndDate(this.getEndDate());
        fixedTransactionEntity.setProduct(this.getProduct());
        fixedTransactionEntity.setPurpose(this.getPurpose());
        fixedTransactionEntity.setIsVariable(this.getIsVariable());
        fixedTransactionEntity.setDay(this.getDay());
        Set<FixedTransactionAmountEntity> transactionAmountEntities = new HashSet<>();
        if (this.getTransactionAmounts() != null) {
            for (TransactionAmount transactionAmount : this.getTransactionAmounts()) {
                transactionAmount.setFixedTransaction(fixedTransactionEntity);
                transactionAmountEntities.add(transactionAmount.toEntity());
            }
        }
        fixedTransactionEntity.setTransactionAmounts(transactionAmountEntities);
        return fixedTransactionEntity;
    }

    @Override
    public CategoryTree getCategoryTree() {
        return categoryTree;
    }

    @Override
    public Set<? extends AttachmentEntity> getAttachments() {
        return this.attachments;
    }

    @Override
    public void setCategoryTree(CategoryTree categoryTree) {
        this.categoryTree = categoryTree;
    }

    @Override
    public Set<TransactionAmount> getTransactionAmounts() {
        return transactionAmounts;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FixedTransaction && ((FixedTransaction) obj).getId() == this.getId();
    }
}
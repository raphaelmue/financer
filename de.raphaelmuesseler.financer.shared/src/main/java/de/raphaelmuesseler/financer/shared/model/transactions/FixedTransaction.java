package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import de.raphaelmuesseler.financer.shared.model.db.FixedTransactionAmountDAO;
import de.raphaelmuesseler.financer.shared.model.db.FixedTransactionDAO;
import de.raphaelmuesseler.financer.shared.model.db.TransactionAttachmentDAO;
import de.raphaelmuesseler.financer.util.date.DateUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FixedTransaction extends FixedTransactionDAO implements Transaction {
    private CategoryTree categoryTree;
    private final Set<Attachment> attachments;
    private final Set<TransactionAmount> transactionAmounts;

    public FixedTransaction(FixedTransactionDAO fixedTransactionDAO) {
        this(fixedTransactionDAO, new CategoryTreeImpl(new Category(fixedTransactionDAO.getCategory())));
    }

    public FixedTransaction(FixedTransactionDAO fixedTransactionDAO, CategoryTree categoryTree) {
        this(fixedTransactionDAO.getId(),
                fixedTransactionDAO.getAmount(),
                categoryTree,
                fixedTransactionDAO.getStartDate(),
                fixedTransactionDAO.getEndDate(),
                fixedTransactionDAO.getProduct(),
                fixedTransactionDAO.getPurpose(),
                fixedTransactionDAO.getIsVariable(),
                fixedTransactionDAO.getDay(),
                new HashSet<>());
        if (fixedTransactionDAO.getTransactionAmounts() != null) {
            for (FixedTransactionAmountDAO transactionAmountDAO : fixedTransactionDAO.getTransactionAmounts()) {
                this.transactionAmounts.add(new TransactionAmount(transactionAmountDAO));
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

    public boolean isActive() {
        return LocalDate.now().compareTo(this.getStartDate()) >= 0 &&
                (this.getEndDate() == null || (LocalDate.now().compareTo(this.getEndDate()) <= 0));
    }

    @Override
    public FixedTransactionDAO toDatabaseAccessObject() {
        FixedTransactionDAO fixedTransactionDAO = new FixedTransactionDAO();
        fixedTransactionDAO.setId(this.getId());
        fixedTransactionDAO.setAmount(this.getAmount());
        fixedTransactionDAO.setCategory(this.getCategoryTree().getValue());
        fixedTransactionDAO.setStartDate(this.getStartDate());
        fixedTransactionDAO.setEndDate(this.getEndDate());
        fixedTransactionDAO.setProduct(this.getProduct());
        fixedTransactionDAO.setPurpose(this.getPurpose());
        fixedTransactionDAO.setIsVariable(this.getIsVariable());
        fixedTransactionDAO.setDay(this.getDay());
        Set<FixedTransactionAmountDAO> transactionAmountDAOS = new HashSet<>();
        if (this.getTransactionAmounts() != null) {
            for (TransactionAmount transactionAmount : this.getTransactionAmounts()) {
                transactionAmount.setFixedTransaction(fixedTransactionDAO);
                transactionAmountDAOS.add(transactionAmount.toDatabaseAccessObject());
            }
        }
        fixedTransactionDAO.setTransactionAmounts(transactionAmountDAOS);
        return fixedTransactionDAO;
    }

    @Override
    public CategoryTree getCategoryTree() {
        return categoryTree;
    }

    @Override
    public Set<? extends TransactionAttachmentDAO> getAttachments() {
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
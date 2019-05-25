package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import de.raphaelmuesseler.financer.shared.model.db.TransactionAttachmentEntity;
import de.raphaelmuesseler.financer.shared.model.db.VariableTransactionEntity;
import de.raphaelmuesseler.financer.util.date.DateUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class VariableTransaction extends VariableTransactionEntity implements Transaction {
    private CategoryTree categoryTree;

    public VariableTransaction(VariableTransactionEntity databaseVariableTransaction) {
        this(databaseVariableTransaction,
                new CategoryTreeImpl(new Category(databaseVariableTransaction.getCategory())));
    }

    public VariableTransaction(VariableTransactionEntity databaseVariableTransaction, CategoryTree categoryTree) {
        this(databaseVariableTransaction.getId(),
                databaseVariableTransaction.getAmount(),
                databaseVariableTransaction.getValueDate(),
                categoryTree,
                databaseVariableTransaction.getProduct(),
                databaseVariableTransaction.getPurpose(),
                databaseVariableTransaction.getShop());
        if (databaseVariableTransaction.getAttachments() != null) {
            this.setAttachments(new HashSet<>());
            for (TransactionAttachmentEntity transactionAttachmentEntity : databaseVariableTransaction.getAttachments()) {
                this.getAttachments().add(new Attachment(transactionAttachmentEntity));
            }
        }
    }

    public VariableTransaction(int id, double amount, LocalDate valueDate, CategoryTree category, String product, String purpose, String shop) {
        this.setId(id);
        this.setAmount(amount);
        this.setValueDate(valueDate);
        this.setCategoryTree(category);
        this.setProduct(product);
        this.setPurpose(purpose);
        this.setShop(shop);
        if (this.categoryTree != null) {
            this.setCategory(this.categoryTree.getValue());
        }
    }

    public CategoryTree getCategoryTree() {
        return categoryTree;
    }

    public void setCategoryTree(CategoryTree categoryTree) {
        this.categoryTree = categoryTree;
    }

    @Override
    public Set<Attachment> getAttachments() {
        //noinspection unchecked
        return (Set<Attachment>) super.getAttachments();
    }

    @Override
    public double getAmount() {
        return super.getAmount();
    }

    @Override
    public double getAmount(LocalDate localDate) {
        return (DateUtil.checkIfMonthsAreEqual(localDate, this.getValueDate()) ? this.getAmount() : 0);
    }

    @Override
    public double getAmount(LocalDate startDate, LocalDate endDate) {
        return (startDate.compareTo(this.getValueDate()) <= 0 && endDate.compareTo(this.getValueDate()) >= 0 ? this.getAmount() : 0);
    }

    @Override
    public VariableTransactionEntity toEntity() {
        VariableTransactionEntity databaseVariableTransaction = new VariableTransactionEntity();
        databaseVariableTransaction.setId(this.getId());
        databaseVariableTransaction.setValueDate(this.getValueDate());
        databaseVariableTransaction.setAmount(this.getAmount());
        databaseVariableTransaction.setCategory(this.getCategory());
        databaseVariableTransaction.setProduct(this.getProduct());
        databaseVariableTransaction.setPurpose(this.getPurpose());
        databaseVariableTransaction.setShop(this.getShop());
        return databaseVariableTransaction;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VariableTransaction && ((VariableTransaction) obj).getId() == this.getId();
    }
}

package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import de.raphaelmuesseler.financer.shared.model.db.VariableTransactionDAO;
import de.raphaelmuesseler.financer.util.date.DateUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class VariableTransaction extends VariableTransactionDAO implements Transaction {
    private CategoryTree categoryTree;
    private final Set<Attachment> attachments;

    public VariableTransaction(VariableTransactionDAO databaseVariableTransaction) {
        this(databaseVariableTransaction,
                new CategoryTreeImpl(new Category(databaseVariableTransaction.getCategory())));
    }

    public VariableTransaction(VariableTransactionDAO databaseVariableTransaction, CategoryTree categoryTree) {
        this(databaseVariableTransaction.getId(),
                databaseVariableTransaction.getAmount(),
                databaseVariableTransaction.getValueDate(),
                categoryTree,
                databaseVariableTransaction.getProduct(),
                databaseVariableTransaction.getPurpose(),
                databaseVariableTransaction.getShop());
    }

    public VariableTransaction(int id, double amount, LocalDate valueDate, CategoryTree category, String product, String purpose, String shop) {
        this.setId(id);
        this.setAmount(amount);
        this.setValueDate(valueDate);
        this.setCategoryTree(category);
        this.setProduct(product);
        this.setPurpose(purpose);
        this.setShop(shop);
        this.attachments = new HashSet<>();
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
        return this.attachments;
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
    public VariableTransactionDAO toDatabaseAccessObject() {
        VariableTransactionDAO databaseVariableTransaction = new VariableTransactionDAO();
        databaseVariableTransaction.setId(this.getId());
        databaseVariableTransaction.setValueDate(this.getValueDate());
        databaseVariableTransaction.setAmount(this.getAmount());
        databaseVariableTransaction.setCategory(this.getCategory());
        databaseVariableTransaction.setProduct(this.getProduct());
        databaseVariableTransaction.setPurpose(this.getPurpose());
        databaseVariableTransaction.setShop(this.getShop());
        return databaseVariableTransaction;
    }
}

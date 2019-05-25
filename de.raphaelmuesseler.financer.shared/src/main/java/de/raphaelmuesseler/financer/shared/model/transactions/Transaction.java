package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.db.DataEntity;
import de.raphaelmuesseler.financer.shared.model.db.TransactionAttachmentEntity;

import java.io.Serializable;
import java.util.Set;

public interface Transaction extends Serializable, AmountProvider, DataEntity {

    /**
     * Returns the amount of this transaction.
     *
     * @return amount
     */
    double getAmount();

    /**
     * Sets the amount of this transaction
     *
     * @param amount amount
     */
    void setAmount(double amount);

    /**
     * Returns the parent category tree to which this transaction belongs.
     *
     * @return category tree
     */
    CategoryTree getCategoryTree();

    /**
     * Sets the category tree of this transaction.
     *
     * @param categoryTree category tree
     */
    void setCategoryTree(CategoryTree categoryTree);

    /**
     * Returns the product of this transaction.
     *
     * @return product
     */
    String getProduct();

    /**
     * Sets the product for this transaction.
     *
     * @param product product
     */
    void setProduct(String product);

    /**
     * Returns the purpose of this transaction.
     *
     * @return purpose
     */
    String getPurpose();

    /**
     * Sets the purpose of this transaction.
     *
     * @param purpose purpose
     */
    void setPurpose(String purpose);

    /**
     * Retuns a set of attachments of this transaction.
     *
     * @return set of attachments
     */
    Set<? extends TransactionAttachmentEntity> getAttachments();

    default void adjustAmountSign() {
        if ((this.getCategoryTree().getValue().getCategoryClass().isRevenue() && this.getAmount() < 0) ||
                (!this.getCategoryTree().getValue().getCategoryClass().isRevenue() && this.getAmount() >= 0)) {
            this.setAmount(this.getAmount() * (-1));
        }
    }
}

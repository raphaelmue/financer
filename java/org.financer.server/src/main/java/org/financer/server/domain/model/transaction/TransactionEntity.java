package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.category.CategoryEntity;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TransactionEntity implements DataEntity, AmountProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_sequence")
    @Column(name = "id")
    private long id;

    @ManyToOne(targetEntity = CategoryEntity.class, optional = false)
    private CategoryEntity category;

    @Embedded
    private Amount amount;

    @Column(name = "product")
    private String product;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "shop")
    private String vendor;

    @OneToMany(mappedBy = "transaction")
    private Set<AttachmentEntity> attachments = new HashSet<>();

    /**
     * Adjusts the amount sign, if necessary. If the category is a revenue category and the amount is negative or vice
     * versa, the amount sign will be changed.
     */
    public void adjustAmountSign() {
        if ((this.getCategory().getCategoryClass().isRevenue() && this.getAmount().isNegative()) ||
                (!this.getCategory().getCategoryClass().isRevenue() && this.getAmount().isPositive())) {
            this.setAmount(new Amount(this.getAmount().getAmount() * (-1)));
        }
    }

    /*
     * Getters and Setters
     */

    @Override
    public long getId() {
        return id;
    }

    public TransactionEntity setId(long id) {
        this.id = id;
        return this;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public TransactionEntity setCategory(CategoryEntity category) {
        this.category = category;
        return this;
    }

    @Override
    public Amount getAmount() {
        return amount;
    }

    public TransactionEntity setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public TransactionEntity setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getPurpose() {
        return purpose;
    }

    public TransactionEntity setPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public TransactionEntity setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public Set<AttachmentEntity> getAttachments() {
        return attachments;
    }

    public TransactionEntity setAttachments(Set<AttachmentEntity> attachments) {
        this.attachments = attachments;
        return this;
    }
}

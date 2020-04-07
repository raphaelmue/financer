package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.user.UserProperty;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Transaction implements DataEntity, AmountProvider, UserProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_sequence")
    @Column(name = "id")
    private long id;

    @ManyToOne(targetEntity = Category.class, optional = false)
    private Category category;

    @Embedded
    private Amount amount;

    @Column(name = "product")
    private String product;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "shop")
    private String vendor;

    @OneToMany(mappedBy = "transaction")
    private Set<Attachment> attachments = new HashSet<>();

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.getCategory().isPropertyOfUser(userId);
    }

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

    public Transaction setId(long id) {
        this.id = id;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Transaction setCategory(Category category) {
        this.category = category;
        return this;
    }

    @Override
    public Amount getAmount() {
        return amount;
    }

    public Transaction setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public Transaction setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getPurpose() {
        return purpose;
    }

    public Transaction setPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public Transaction setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public Transaction setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }
}

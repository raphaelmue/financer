package org.financer.server.domain.model.transaction;

import org.financer.server.application.api.error.IllegalTransactionCategoryClassException;
import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.user.UserProperty;
import org.financer.shared.domain.model.AmountProvider;

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

    @Column(name = "description")
    private String description;

    @Column(name = "vendor")
    private String vendor;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Attachment> attachments = new HashSet<>();

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.getCategory().isPropertyOfUser(userId);
    }

    public abstract boolean isCategoryClassValid(Category category);

    public final boolean isCategoryClassValid() {
        return isCategoryClassValid(this.category);
    }

    public final void throwIfInvalidCategoryClass() {
        if (!isCategoryClassValid()) {
            throw new IllegalTransactionCategoryClassException(this);
        }
    }

    @Override
    public boolean isFixed() {
        return this.category.isFixed();
    }

    @Override
    public boolean isRevenue() {
        return this.category.isRevenue();
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

    public String getDescription() {
        return description;
    }

    public Transaction setDescription(String purpose) {
        this.description = purpose;
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

    @Override
    public String toString() {
        return "Transaction[" + "id=" + id + ", category=" + category + ", vendor='" + vendor + '\'' + ']';
    }
}

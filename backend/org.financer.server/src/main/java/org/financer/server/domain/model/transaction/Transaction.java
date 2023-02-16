package org.financer.server.domain.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.financer.server.application.api.error.IllegalTransactionCategoryClassException;
import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.user.UserProperty;
import org.financer.shared.domain.model.AmountProvider;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Transaction implements DataEntity, AmountProvider, UserProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_sequence")
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

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

    public Transaction addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        return this;
    }

    public Transaction removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
        return this;
    }
}

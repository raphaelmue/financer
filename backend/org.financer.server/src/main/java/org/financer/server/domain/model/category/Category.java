package org.financer.server.domain.model.category;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.financer.server.application.api.error.IllegalCategoryParentStateException;
import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.transaction.Transaction;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.model.user.UserProperty;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.financer.util.collections.Tree;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "categories")
public class Category implements DataEntity, Tree, AmountProvider, UserProperty {
    private static final long serialVersionUID = 5491420625985358596L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(targetEntity = User.class, optional = false)
    @ToString.Exclude
    private User user;

    @Embedded
    private CategoryClass categoryClass;

    @ManyToOne(targetEntity = Category.class)
    @ToString.Exclude
    private Category parent;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Category> children;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private Set<Transaction> transactions = new HashSet<>();

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.getUser().getId() == userId;
    }

    @Override
    public Amount getTotalAmount() {
        Amount amount = new Amount();

        for (AmountProvider amountProvider : this.transactions) {
            amount = amount.add(amountProvider.getTotalAmount());
        }
        if (!this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getTotalAmount());
            }
        }

        return amount;
    }

    @Override
    public Amount getTotalAmount(ValueDate valueDate) {
        Amount amount = new Amount();

        for (AmountProvider amountProvider : this.transactions) {
            amount = amount.add(amountProvider.getTotalAmount(valueDate));
        }
        if (!this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getTotalAmount(valueDate));
            }
        }

        return amount;
    }

    @Override
    public Amount getTotalAmount(TimeRange timeRange) {
        Amount amount = new Amount();

        for (AmountProvider amountProvider : this.transactions) {
            amount = amount.add(amountProvider.getTotalAmount(timeRange));
        }
        if (!this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getTotalAmount(timeRange));
            }
        }

        return amount;
    }

    @Override
    public boolean isFixed() {
        return this.categoryClass.isFixed();
    }

    @Override
    public boolean isRevenue() {
        return this.categoryClass.isRevenue();
    }

    @Override
    public void adjustAmountSign() {
        for (AmountProvider amountProvider : this.transactions) {
            amountProvider.adjustAmountSign();
        }
        if (!this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amountProvider.adjustAmountSign();
            }
        }
    }

    public void throwIfParentCategoryClassIsInvalid() {
        if (this.parent != null && !this.parent.getCategoryClass().equals(this.categoryClass)) {
            throw new IllegalCategoryParentStateException(this, this.parent);
        }
    }

    @Override
    public Category setParent(Tree parent) {
        this.parent = (Category) parent;
        return this;
    }
}

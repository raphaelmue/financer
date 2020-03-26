package org.financer.server.domain.model.category;

import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.transaction.TransactionEntity;
import org.financer.server.domain.model.user.UserEntity;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.util.collections.Tree;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users_categories")
public class CategoryEntity implements DataEntity, Tree, AmountProvider {
    private static final long serialVersionUID = 5491420625985358596L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    private UserEntity user;

    @Embedded
    private CategoryClass categoryClass;

    @ManyToOne(targetEntity = CategoryEntity.class)
    private CategoryEntity parent;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "parent")
    private Set<CategoryEntity> children;

    @OneToMany(mappedBy = "category")
    private Set<TransactionEntity> transactions = new HashSet<>();

    @Override
    public Amount getAmount() {
        Amount amount = new Amount();

        if (this.isLeaf()) {
            for (AmountProvider amountProvider : this.transactions) {
                amount = amount.add(amountProvider.getAmount());
            }
        } else {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getAmount());
            }
        }

        return amount;
    }

    @Override
    public Amount getAmount(ValueDate valueDate) {
        Amount amount = new Amount();

        if (this.isLeaf()) {
            for (AmountProvider amountProvider : this.transactions) {
                amount = amount.add(amountProvider.getAmount(valueDate));
            }
        } else {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getAmount(valueDate));
            }
        }

        return amount;
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        Amount amount = new Amount();

        if (this.isLeaf()) {
            for (AmountProvider amountProvider : this.transactions) {
                amount = amount.add(amountProvider.getAmount(timeRange));
            }
        } else {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getAmount(timeRange));
            }
        }

        return amount;
    }

    /*
     * Getters and Setters
     */

    @Override
    public long getId() {
        return id;
    }

    public CategoryEntity setId(long id) {
        this.id = id;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public CategoryEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public CategoryClass getCategoryClass() {
        return categoryClass;
    }

    public CategoryEntity setCategoryClass(CategoryClass categoryClass) {
        this.categoryClass = categoryClass;
        return this;
    }

    @Override
    public CategoryEntity getParent() {
        return parent;
    }

    @Override
    public CategoryEntity setParent(Tree parent) {
        this.parent = (CategoryEntity) parent;
        return this;
    }

    public String getName() {
        return name;
    }

    public CategoryEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Set<CategoryEntity> getChildren() {
        return children;
    }

    public CategoryEntity setChildren(Set<CategoryEntity> children) {
        this.children = children;
        return this;
    }
}

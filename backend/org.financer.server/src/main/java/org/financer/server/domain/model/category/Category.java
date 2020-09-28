package org.financer.server.domain.model.category;

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
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category implements DataEntity, Tree, AmountProvider, UserProperty {
    private static final long serialVersionUID = 5491420625985358596L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(targetEntity = User.class, optional = false)
    private User user;

    @Embedded
    private CategoryClass categoryClass;

    @ManyToOne(targetEntity = Category.class)
    private Category parent;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> children;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.getUser().getId() == userId;
    }

    @Override
    public Amount getAmount() {
        Amount amount = new Amount();

        for (AmountProvider amountProvider : this.transactions) {
            amount = amount.add(amountProvider.getAmount());
        }
        if (this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getAmount());
            }
        }

        return amount;
    }

    @Override
    public Amount getAmount(ValueDate valueDate) {
        Amount amount = new Amount();

        for (AmountProvider amountProvider : this.transactions) {
            amount = amount.add(amountProvider.getAmount(valueDate));
        }
        if (this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getAmount(valueDate));
            }
        }

        return amount;
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        Amount amount = new Amount();

        for (AmountProvider amountProvider : this.transactions) {
            amount = amount.add(amountProvider.getAmount(timeRange));
        }
        if (!this.isLeaf()) {
            for (AmountProvider amountProvider : this.children) {
                amount = amount.add(amountProvider.getAmount(timeRange));
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

    /*
     * Getters and Setters
     */

    @Override
    public long getId() {
        return id;
    }

    public Category setId(long id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Category setUser(User user) {
        this.user = user;
        return this;
    }

    public CategoryClass getCategoryClass() {
        return categoryClass;
    }

    public Category setCategoryClass(CategoryClass categoryClass) {
        this.categoryClass = categoryClass;
        return this;
    }

    @Override
    public Category getParent() {
        return parent;
    }

    public Category setParent(Category parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public Category setParent(Tree parent) {
        return this.setParent((Category) parent);
    }

    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Set<Category> getChildren() {
        return children;
    }

    public Category setChildren(Set<Category> children) {
        this.children = children;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category[" + "id=" + id + ", categoryClass=" + categoryClass + ", name='" + name + '\'' + ']';
    }
}

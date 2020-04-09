package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product implements DataEntity, AmountProvider {
    private static final long serialVersionUID = -3732346378113344348L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(targetEntity = VariableTransaction.class, optional = false)
    private VariableTransaction transaction;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Embedded
    private Quantity quantity;

    @Embedded
    private Amount amount;

    @Override
    public Amount getAmount() {
        return this.amount.calculate(this.quantity);
     }

    @Override
    public Amount getAmount(ValueDate valueDate) {
        if (this.transaction.getValueDate().isInSameMonth(valueDate)) {
            return this.amount.calculate(this.quantity);
        }
        return new Amount();
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        if (timeRange.includes(this.transaction.getValueDate())) {
            return this.amount.calculate(this.quantity);
        }
        return new Amount();
    }

    @Override
    public boolean isFixed() {
        return this.transaction.isFixed();
    }

    @Override
    public boolean isRevenue() {
        return this.transaction.isRevenue();
    }

    @Override
    public void adjustAmountSign() {
        if ((this.isRevenue() == this.getAmount().isNegative())) {
            this.setAmount(this.getAmount().adjustSign());
        }
    }

    /*
     * Getters and Setters
     */

    @Override
    public long getId() {
        return id;
    }

    public Product setId(long id) {
        this.id = id;
        return this;
    }

    public VariableTransaction getTransaction() {
        return transaction;
    }

    public Product setTransaction(VariableTransaction transaction) {
        this.transaction = transaction;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Product setQuantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }

    public Product setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product [" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ']';
    }


}

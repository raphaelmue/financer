package org.financer.server.domain.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.user.UserProperty;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "products")
public class Product implements DataEntity, AmountProvider, UserProperty {
    private static final long serialVersionUID = -3732346378113344348L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(targetEntity = VariableTransaction.class, optional = false)
    @JoinColumn(name = "variable_transaction_id")
    @ToString.Exclude
    private VariableTransaction variableTransaction;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Embedded
    private Quantity quantity;

    @Embedded
    private Amount amount;

    @Override
    public Amount getTotalAmount() {
        return this.amount.calculate(this.quantity);
    }

    @Override
    public Amount getTotalAmount(ValueDate valueDate) {
        if (this.variableTransaction.getValueDate().isInSameMonth(valueDate)) {
            return this.amount.calculate(this.quantity);
        }
        return new Amount();
    }

    @Override
    public Amount getTotalAmount(TimeRange timeRange) {
        if (timeRange.includes(this.variableTransaction.getValueDate())) {
            return this.amount.calculate(this.quantity);
        }
        return new Amount();
    }

    @Override
    public boolean isFixed() {
        return this.variableTransaction.isFixed();
    }

    @Override
    public boolean isRevenue() {
        return this.variableTransaction.isRevenue();
    }

    @Override
    public void adjustAmountSign() {
        if (this.isRevenue() == this.getAmount().isNegative()) {
            this.setAmount(this.getAmount().adjustSign());
        }
    }

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.variableTransaction.isPropertyOfUser(userId);
    }
}

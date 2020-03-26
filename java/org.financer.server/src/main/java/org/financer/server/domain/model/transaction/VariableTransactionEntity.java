package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.category.CategoryEntity;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "transactions")
public class VariableTransactionEntity extends TransactionEntity {
    private static final long serialVersionUID = -118658876074097774L;

    @Override
    public Amount getAmount(ValueDate valueDate) {
        if (this.getValueDate().isInSameMonth(valueDate)) {
            return this.getAmount();
        } else {
            return new Amount(0);
        }
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        if (timeRange.includes(this.getValueDate())) {
            return this.getAmount();
        } else {
            return new Amount(0);
        }
    }

    /*
     * Getters and Setters
     */

    @Override
    public VariableTransactionEntity setId(long id) {
        super.setId(id);
        return this;
    }

    @Override
    public VariableTransactionEntity setCategory(CategoryEntity category) {
        super.setCategory(category);
        return this;
    }

    @Override
    public VariableTransactionEntity setValueDate(ValueDate valueDate) {
        super.setValueDate(valueDate);
        return this;
    }

    @Override
    public VariableTransactionEntity setAmount(Amount amount) {
        super.setAmount(amount);
        return this;
    }

    @Override
    public VariableTransactionEntity setProduct(String product) {
        super.setProduct(product);
        return this;
    }

    @Override
    public VariableTransactionEntity setPurpose(String purpose) {
        super.setPurpose(purpose);
        return this;
    }

    @Override
    public VariableTransactionEntity setVendor(String vendor) {
        super.setVendor(vendor);
        return this;
    }

    @Override
    public VariableTransactionEntity setAttachments(Set<AbstractAttachment> attachments) {
        super.setAttachments(attachments);
        return this;
    }
}

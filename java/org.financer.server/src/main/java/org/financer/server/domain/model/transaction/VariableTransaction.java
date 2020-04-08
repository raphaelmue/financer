package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "variable_transactions")
public class VariableTransaction extends Transaction {
    private static final long serialVersionUID = -118658876074097774L;

    @Embedded
    private ValueDate valueDate;

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

    @Override
    public boolean isCategoryClassValid(Category category) {
        return category.isVariable();
    }

    /*
     * Getters and Setters
     */

    public ValueDate getValueDate() {
        return valueDate;
    }

    public VariableTransaction setValueDate(ValueDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    @Override
    public VariableTransaction setId(long id) {
        super.setId(id);
        return this;
    }

    @Override
    public VariableTransaction setCategory(Category category) {
        super.setCategory(category);
        return this;
    }

    @Override
    public VariableTransaction setAmount(Amount amount) {
        super.setAmount(amount);
        return this;
    }

    @Override
    public VariableTransaction setProduct(String product) {
        super.setProduct(product);
        return this;
    }

    @Override
    public VariableTransaction setPurpose(String purpose) {
        super.setPurpose(purpose);
        return this;
    }

    @Override
    public VariableTransaction setVendor(String vendor) {
        super.setVendor(vendor);
        return this;
    }

    @Override
    public VariableTransaction setAttachments(Set<Attachment> attachments) {
        super.setAttachments(attachments);
        return this;
    }
}

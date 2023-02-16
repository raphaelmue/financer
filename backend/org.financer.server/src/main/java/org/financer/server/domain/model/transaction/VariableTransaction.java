package org.financer.server.domain.model.transaction;

import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.category.Category;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "variable_transactions")
public final class VariableTransaction extends Transaction {
    private static final long serialVersionUID = -118658876074097774L;

    @Embedded
    private ValueDate valueDate;

    @OneToMany(mappedBy = "variableTransaction", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    @Override
    public Amount getTotalAmount() {
        Amount amount = new Amount();
        for (AmountProvider amountProvider : this.products) {
            amount = amount.add(amountProvider.getTotalAmount());
        }
        return amount;
    }

    @Override
    public Amount getTotalAmount(ValueDate valueDate) {
        Amount amount = new Amount();
        for (AmountProvider amountProvider : this.products) {
            amount = amount.add(amountProvider.getTotalAmount(valueDate));
        }
        return amount;
    }

    @Override
    public Amount getTotalAmount(TimeRange timeRange) {
        Amount amount = new Amount();
        for (AmountProvider amountProvider : this.products) {
            amount = amount.add(amountProvider.getTotalAmount(timeRange));
        }
        return amount;
    }

    @Override
    public void adjustAmountSign() {
        for (AmountProvider amountProvider : this.getProducts()) {
            amountProvider.adjustAmountSign();
        }
    }

    @Override
    public boolean isCategoryClassValid(Category category) {
        return category.getIsVariable();
    }

    public VariableTransaction addProduct(Product product) {
        this.products.add(product);
        return this;
    }

    /*
     * Setters
     */

    @Override
    public VariableTransaction setId(Long id) {
        super.setId(id);
        return this;
    }

    @Override
    public VariableTransaction setCategory(Category category) {
        super.setCategory(category);
        return this;
    }

    @Override
    public VariableTransaction setDescription(String purpose) {
        super.setDescription(purpose);
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

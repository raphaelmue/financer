package org.financer.client.domain.model.transaction;

import org.financer.client.domain.model.category.Category;
import org.financer.shared.domain.model.AmountProvider;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

import java.util.HashSet;
import java.util.Set;

public final class VariableTransaction extends Transaction {

    private ValueDate valueDate;
    private Set<Product> products = new HashSet<>();

    @Override
    public Amount getAmount() {
        Amount amount = new Amount();
        for (AmountProvider amountProvider : this.products) {
            amount = amount.add(amountProvider.getAmount());
        }
        return amount;
    }

    @Override
    public Amount getAmount(ValueDate valueDate) {
        Amount amount = new Amount();
        for (AmountProvider amountProvider : this.products) {
            amount = amount.add(amountProvider.getAmount(valueDate));
        }
        return amount;
    }

    @Override
    public Amount getAmount(TimeRange timeRange) {
        Amount amount = new Amount();
        for (AmountProvider amountProvider : this.products) {
            amount = amount.add(amountProvider.getAmount(timeRange));
        }
        return amount;
    }

    @Override
    public void adjustAmountSign() {
        for (AmountProvider amountProvider : this.getProducts()) {
            amountProvider.adjustAmountSign();
        }
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

    public Set<Product> getProducts() {
        return products;
    }

    public VariableTransaction setProducts(Set<Product> products) {
        this.products = products;
        return this;
    }

    public VariableTransaction addProduct(Product product) {
        this.products.add(product);
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

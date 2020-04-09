package org.financer.shared.domain.model.api.transaction.variable;

import org.financer.shared.domain.model.api.DataTransferObject;

import java.time.LocalDate;

public class UpdateVariableTransactionDTO implements DataTransferObject {

    private int categoryId = -1;

    private LocalDate valueDate = null;

    private String description = null;

    private String vendor = null;

    public int getCategoryId() {
        return categoryId;
    }

    public UpdateVariableTransactionDTO setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public UpdateVariableTransactionDTO setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UpdateVariableTransactionDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public UpdateVariableTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }
}

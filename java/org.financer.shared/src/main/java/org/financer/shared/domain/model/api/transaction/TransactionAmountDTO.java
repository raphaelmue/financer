package org.financer.shared.domain.model.api.transaction;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class TransactionAmountDTO {

    @NotNull
    @Min(1)
    private int id;

    private LocalDate valueDate;

    private double amount;

    public int getId() {
        return id;
    }

    public TransactionAmountDTO setId(int id) {
        this.id = id;
        return this;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public TransactionAmountDTO setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionAmountDTO setAmount(double amount) {
        this.amount = amount;
        return this;
    }
}

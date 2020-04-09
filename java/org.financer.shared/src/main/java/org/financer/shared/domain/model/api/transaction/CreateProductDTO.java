package org.financer.shared.domain.model.api.transaction;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;

import javax.validation.constraints.NotNull;

public class CreateProductDTO implements DataTransferObject {

    @NotNull
    private String name;

    @NotNull
    private Amount amount;

    @NotNull
    private Quantity quantity;

    public String getName() {
        return name;
    }

    public CreateProductDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Amount getAmount() {
        return amount;
    }

    public CreateProductDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public CreateProductDTO setQuantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }
}

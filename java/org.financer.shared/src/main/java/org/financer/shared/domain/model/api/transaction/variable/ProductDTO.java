package org.financer.shared.domain.model.api.transaction.variable;

import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ProductDTO {

    @NotNull
    @Min(1)
    private int id;

    @NotNull
    private String name;

    @NotNull
    private Amount amount;

    @NotNull
    private Quantity quantity;

    public int getId() {
        return id;
    }

    public ProductDTO setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProductDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Amount getAmount() {
        return amount;
    }

    public ProductDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public ProductDTO setQuantity(Quantity quantity) {
        this.quantity = quantity;
        return this;
    }
}

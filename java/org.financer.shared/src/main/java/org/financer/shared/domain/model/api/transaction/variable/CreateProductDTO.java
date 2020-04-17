package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;

import javax.validation.constraints.NotNull;

@Schema(name = "CreateProduct", defaultValue = "Schema for creating a new product")
public class CreateProductDTO implements DataTransferObject {

    @NotNull
    @Schema(description = "Name of the product", required = true, example = "Burger")
    private String name;

    @NotNull
    @Schema(description = "Amount of the product", required = true, example = "20.0")
    private Amount amount;

    @NotNull
    @Schema(description = "Quanity of the product", required = true, example = "2")
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

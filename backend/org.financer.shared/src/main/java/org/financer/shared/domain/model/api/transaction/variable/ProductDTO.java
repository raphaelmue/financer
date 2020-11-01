package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.AmountProviderDTO;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Schema(name = "Product", description = "Schema of a product")
public class ProductDTO extends AmountProviderDTO implements DataTransferObject {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the product", required = true, example = "1")
    private int id;

    @NotNull
    @Schema(description = "Name of the product", required = true, example = "Burger")
    private String name;

    @NotNull
    @Schema(description = "Amount of the product", required = true, example = "20.0")
    private Amount amount;

    @NotNull
    @Schema(description = "Quanity of the product", required = true, example = "2")
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

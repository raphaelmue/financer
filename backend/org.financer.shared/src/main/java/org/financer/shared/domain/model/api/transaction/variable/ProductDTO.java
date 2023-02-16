package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.AmountProviderDTO;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Schema(name = "Product", description = "Schema of a product")
public class ProductDTO implements AmountProviderDTO {

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

    private Amount totalAmount;

    @Override
    public void setTotalAmount(Amount totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public Amount getTotalAmount() {
        return totalAmount;
    }
}

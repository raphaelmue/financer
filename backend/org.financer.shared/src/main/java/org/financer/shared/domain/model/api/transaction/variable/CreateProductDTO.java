package org.financer.shared.domain.model.api.transaction.variable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.Quantity;

import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Schema(name = "CreateProduct", description = "Schema for creating a new product")
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
}

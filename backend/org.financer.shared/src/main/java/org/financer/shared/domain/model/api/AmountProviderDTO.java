package org.financer.shared.domain.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.value.objects.Amount;

public abstract class AmountProviderDTO {

    @Schema(description = "Aggregated amount", required = true, example = "20.0")
    private Amount totalAmount;

    public Amount getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Amount totalAmount) {
        this.totalAmount = totalAmount;
    }
}

package org.financer.shared.domain.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.value.objects.Amount;

public interface AmountProviderDTO extends DataTransferObject {

    @Schema(description = "Aggregated amount", required = true, example = "20.0")
    Amount getTotalAmount();

    void setTotalAmount(Amount totalAmount);
}

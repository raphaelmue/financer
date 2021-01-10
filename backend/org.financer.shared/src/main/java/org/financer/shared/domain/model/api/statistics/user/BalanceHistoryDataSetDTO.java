package org.financer.shared.domain.model.api.statistics.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.hateoas.RepresentationModel;

import java.util.Map;

@Data
@Accessors(chain = true)
@Schema(name = "BalanceHistory", description = "Balance history for user")
public class BalanceHistoryDataSetDTO extends RepresentationModel<BalanceHistoryDataSetDTO> {

    @Schema(description = "Balance history records")
    private Map<String, Map<String, Double>> records;

}

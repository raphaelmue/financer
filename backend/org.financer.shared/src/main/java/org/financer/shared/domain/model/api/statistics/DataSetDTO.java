package org.financer.shared.domain.model.api.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.springframework.hateoas.RepresentationModel;

import java.util.Map;

@Data
@Accessors(chain = true)
@Schema(name = "DataSet", description = "Set of records")
public class DataSetDTO extends RepresentationModel<DataSetDTO> implements DataTransferObject {

    @Schema(description = "Data Records")
    private Map<String, Map<String, Double>> records;

}

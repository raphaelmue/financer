package org.financer.shared.domain.model.api.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.SettingPair;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Accessors(chain = true)
@Schema(name = "UpdateSettings", description = "Schema for updating users settings")
public class UpdateSettingsDTO implements DataTransferObject {

    @NotNull
    @Schema(description = "Settings that will be updated", required = true)
    private Map<SettingPair.Property, String> settings;

}

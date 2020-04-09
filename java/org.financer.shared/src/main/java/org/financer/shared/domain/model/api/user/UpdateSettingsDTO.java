package org.financer.shared.domain.model.api.user;

import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.SettingPair;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class UpdateSettingsDTO implements DataTransferObject {

    @NotNull
    private Map<SettingPair.Property, String> settings;

    public Map<SettingPair.Property, String> getSettings() {
        return settings;
    }

    public UpdateSettingsDTO setSettings(Map<SettingPair.Property, String> settings) {
        this.settings = settings;
        return this;
    }

}

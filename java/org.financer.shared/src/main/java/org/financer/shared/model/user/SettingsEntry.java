package org.financer.shared.model.user;

import org.financer.shared.model.db.SettingsEntity;

public class SettingsEntry extends SettingsEntity {
    private static final long serialVersionUID = -17069773960916426L;

    public SettingsEntry(int id, User user, Settings.Property property) {
        this(id, user, property, null);
    }

    public SettingsEntry(int id, User user, Settings.Property property, String value) {
        this.setId(id);
        this.setUser(user);
        this.setProperty(property);
        this.setValue(value);
    }

    private void setProperty(Settings.Property property) {
        super.setProperty(property.getName());
    }

    @Override
    public SettingsEntity toEntity() {
        SettingsEntity settingsEntity = new SettingsEntity();
        settingsEntity.setId(this.getId());
        settingsEntity.setUser(this.getUser());
        settingsEntity.setProperty(this.getProperty());
        settingsEntity.setValue(this.getValue());
        return settingsEntity;
    }

    @Override
    public String getValue() {
        if (super.getValue() != null) {
            return super.getValue();
        } else {
            return Settings.Property.getPropertyByName(this.getProperty()).getDefaultValue();
        }
    }
}

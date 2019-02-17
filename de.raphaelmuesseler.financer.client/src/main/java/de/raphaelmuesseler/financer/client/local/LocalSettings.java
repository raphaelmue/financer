package de.raphaelmuesseler.financer.client.local;

import de.raphaelmuesseler.financer.shared.model.user.Settings;

import java.util.Locale;

public interface LocalSettings extends Settings {

    Locale getLanguage();

    void setLanguage(Locale locale);

    String getTheme();

}


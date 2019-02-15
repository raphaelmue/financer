package de.raphaelmuesseler.financer.client.local;

import java.util.Locale;

public interface LocalSettings {

    Locale getLanguage();

    void setLanguage(Locale locale);

    String getTheme();

}


package de.raphaelmuesseler.financer.client.javafx.util;

import de.raphaelmuesseler.financer.client.local.Settings;

import java.util.Locale;

public class ApplicationHelper {

    public static Locale getLocale(Settings settings) {
        Locale locale;
        if (settings != null) {
            locale = settings.getLanguage();
        } else {
            locale = Locale.ENGLISH;
        }
        return locale;
    }

}

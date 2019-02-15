package de.raphaelmuesseler.financer.client.javafx.util;

import de.raphaelmuesseler.financer.client.local.LocalSettings;
import de.raphaelmuesseler.financer.shared.model.user.UserSettings;

import java.util.Locale;

public class ApplicationHelper {

    public static Locale getLocale(LocalSettings settings) {
        Locale locale;
        if (settings != null) {
            locale = settings.getLanguage();
        } else {
            locale = Locale.ENGLISH;
        }
        return locale;
    }

}

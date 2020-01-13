package de.raphaelmuesseler.financer.client.format;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.model.user.User;

import java.text.MessageFormat;
import java.util.*;

/**
 * I18N utility class..
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public final class I18N {
    /**
     * the current selected Locale.
     */
    private static LocalStorage localStorage;

    public static void setLocalStorage(LocalStorage localStorage) {
        I18N.localStorage = localStorage;
    }

    public enum Language {
        ENGLISH("English", Locale.ENGLISH),
        GERMAN("German", Locale.GERMAN);

        private final String name;
        private final Locale locale;

        Language(String name, Locale locale) {
            this.name = name;
            this.locale = locale;
        }

        public String getName() {
            return name;
        }

        public Locale getLocale() {
            return locale;
        }

        public static List<Language> getAll() {
            return Arrays.asList(values());
        }

        public static Language getLanguageByLocale(Locale locale) {
            for (Language language : values()) {
                if (language.getLocale().equals(locale)) {
                    return language;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    /**
     * getObject the supported Locales.
     *
     * @return List of Locale objects.
     */
    private static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));
    }

    /**
     * getObject the default locale. This is the systems default if contained in the supported locales, english otherwise.
     *
     * @return default locale (en)
     */
    private static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    private static Locale getLocale() {
        if (I18N.localStorage.readObject("user") != null) {
            return ((User) I18N.localStorage.readObject("user")).getSettings().getLanguage();
        } else {
            return Locale.ENGLISH;
        }
    }

    /**
     * gets the string with the given key from the resource bundle for the current locale and uses it as first argument
     * to MessageFormat.format, passing in the optional args and returning the result.
     *
     * @param key  message key
     * @param args optional arguments for the message
     * @return localized formatted string
     */
    public static String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("Financer", getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }
}
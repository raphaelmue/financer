package de.raphaelmuesseler.financer.client.local;

import java.io.Serializable;
import java.util.Locale;

public class LocalSettingsImpl implements Serializable, LocalSettings {
    private static final long serialVersionUID = -7328386947589105017L;

    private Locale language;
    private String theme;

    @Override
    public Locale getLanguage() {
        if (this.language ==  null) {
            this.language = Locale.ENGLISH;
        }
        return language;
    }

    @Override
    public String getValueByProperty(String property) {
        switch (property) {
            case "language":
                return this.getLanguage().toLanguageTag();
            case "theme":
                return this.getTheme();
        }
        return null;
    }

    @Override
    public String getTheme() {
        return getTheme();
    }

    @Override
    public void setValueByProperty(String property, String value) {
        switch (property) {
            case "language":
                this.setLanguage(Locale.forLanguageTag(value));
                break;
            case "theme":
                this.setTheme(value);
                break;
        }
    }

    @Override
    public void setLanguage(Locale language) {
        this.language = language;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}

package de.raphaelmuesseler.financer.client.local;

import java.io.Serializable;
import java.util.Locale;

public class Settings implements Serializable {
    private static final long serialVersionUID = 2201611667506790486L;
    private Locale language = Locale.ENGLISH;
    private String theme = "Main Theme";

    public Locale getLanguage() {
        return language;
    }

    public String getTheme() {
        return theme;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}

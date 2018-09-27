package de.raphaelmuesseler.financer.client.local;

import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerAlert;
import de.raphaelmuesseler.financer.client.ui.format.I18N;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;

public class Settings implements Serializable {
    private static final long serialVersionUID = 2201611667506790486L;
    private Locale language = Locale.ENGLISH;
    private Currency currency;
    private boolean showCurrencySign;
    private String theme = "Main Theme";

    public static Settings getSettings() {
        return LocalStorage.getSettings();
    }

    public void save() {
        if (!LocalStorage.writeSettings(this)) {
            Platform.runLater(() -> new FinancerAlert(Alert.AlertType.ERROR, I18N.get("settings"), I18N.get("errSettingsCouldNotBeSaved")));
        }
    }

    public Locale getLanguage() {
        return language;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getTheme() {
        return theme;
    }

    public boolean isShowCurrencySign() {
        return showCurrencySign;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setShowCurrencySign(boolean showCurrencySign) {
        this.showCurrencySign = showCurrencySign;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}

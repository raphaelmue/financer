package de.raphaelmuesseler.financer.shared.model.user;

import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;

public class UserSettings implements Serializable {
    private static final long serialVersionUID = 2201611667506790486L;
    private Currency currency;
    private boolean showCurrencySign;
    private String theme = "Main Theme";

    public Currency getCurrency() {
        return currency;
    }

    public String getTheme() {
        return theme;
    }

    public boolean isShowCurrencySign() {
        return showCurrencySign;
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

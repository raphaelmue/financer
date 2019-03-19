package de.raphaelmuesseler.financer.shared.model.user;

import java.io.Serializable;
import java.util.Currency;

public class UserSettings implements Serializable, Settings {
    private static final long serialVersionUID = 2201611667506790486L;
    private Currency currency;
    private boolean showCurrencySign;
    private String theme = "Main Theme";

    @Override
    public String getValueByProperty(String property) {
        switch (property) {
            case "currency":
                return this.getCurrency().getCurrencyCode();
            case "showCurrencySign":
                return Boolean.toString(this.isShowCurrencySign());
        }
        return null;
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

    @Override
    public void setValueByProperty(String property, String value) {
        switch (property) {
            case "currency":
                this.setCurrency(Currency.getInstance(value));
                break;
            case "showCurrencySign":
                this.setShowCurrencySign(Boolean.valueOf(value));
                break;
        }
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

package de.raphaelmuesseler.financer.shared.model.user;

import java.io.Serializable;
import java.util.Currency;

public class UserSettings implements Serializable, Settings {
    private static final long serialVersionUID = 2201611667506790486L;

    // GENERAL SETTINGS \\

    private Currency currency;
    private boolean showCurrencySign;
    private String theme = "Main Theme";

    // TRANSACTION SETTINGS \\
    private boolean changeAmountSignAutomatically = false;

    @Override
    public String getValueByProperty(String property) {
        switch (property) {
            case "currency":
                return this.getCurrency().getCurrencyCode();
            case "theme":
                return this.getTheme();
            case "showCurrencySign":
                return Boolean.toString(this.isShowCurrencySign());
            case "changeAmountSignAutomatically":
                return Boolean.toString(this.isChangeAmountSignAutomatically());
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

    public boolean isChangeAmountSignAutomatically() {
        return changeAmountSignAutomatically;
    }

    @Override
    public void setValueByProperty(String property, String value) {
        switch (property) {
            case "currency":
                this.setCurrency(Currency.getInstance(value));
                break;
            case "theme":
                this.setTheme(value);
                break;
            case "showCurrencySign":
                this.setShowCurrencySign(Boolean.valueOf(value));
                break;
            case "changeAmountSignAutomatically":
                this.setChangeAmountSignAutomatically(Boolean.valueOf(value));
                break;
        }
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setShowCurrencySign(boolean showCurrencySign) {
        this.showCurrencySign = showCurrencySign;
    }

    public void setChangeAmountSignAutomatically(boolean changeAmountSignAutomatically) {
        this.changeAmountSignAutomatically = changeAmountSignAutomatically;
    }
}

package de.raphaelmuesseler.financer.shared.model.user;

import java.io.Serializable;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserSettings implements Serializable, Settings {
    private static final long serialVersionUID = 2201611667506790486L;
    private final Map<String, String> properties = new HashMap<>();

    @Override
    public String getValueByProperty(Property property) {
        return this.properties.getOrDefault(property.getName(), property.getDefaultValue());
    }

    public Locale getLanguage() {
        return Locale.forLanguageTag(this.getValueByProperty(Property.LANGUAGE));
    }

    public Currency getCurrency() {
        return Currency.getInstance(this.getValueByProperty(Property.CURRENCY));
    }

    public String getTheme() {
        return this.getValueByProperty(Property.THEME);
    }

    public boolean isShowCurrencySign() {
        return Boolean.valueOf(this.getValueByProperty(Property.SHOW_CURRENCY_SIGN));
    }

    public boolean isChangeAmountSignAutomatically() {
        return Boolean.valueOf(this.getValueByProperty(Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY));
    }

    @Override
    public void setValueByProperty(Property property, String value) {
        this.properties.put(property.getName(), value);
    }

    public void setLanguage(Locale locale) {
        this.properties.put(Property.LANGUAGE.getName(), locale.toLanguageTag());
    }

    public void setCurrency(Currency currency) {
        this.setValueByProperty(Property.CURRENCY, currency.getCurrencyCode());
    }


    public void setShowCurrencySign(boolean showCurrencySign) {
        this.setValueByProperty(Property.SHOW_CURRENCY_SIGN, Boolean.toString(showCurrencySign));
    }

    public void setTheme(String theme) {
        this.setValueByProperty(Property.THEME, theme);
    }

    public void setChangeAmountSignAutomatically(boolean changeAmountSignAutomatically) {
        this.setValueByProperty(Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY, Boolean.toString(changeAmountSignAutomatically));
    }
}

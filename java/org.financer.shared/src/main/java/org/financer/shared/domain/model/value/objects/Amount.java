package org.financer.shared.domain.model.value.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.Formattable;
import org.financer.shared.domain.model.Settings;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

@Embeddable
@Immutable
@Schema(description = "Value object for amount")
public class Amount implements Serializable, Formattable {
    private static final long serialVersionUID = 8647653287643900256L;

    @Column(name = "amount", nullable = false)
    @Schema(description = "Amount property", required = true, example = "50.0")
    private final double amount;

    public Amount() {
        this.amount = 0;
    }

    public Amount(int amount) {
        this.amount = amount;
    }

    public Amount(double amount) {
        this.amount = amount;
    }

    @JsonIgnore
    public boolean isPositive() {
        return this.amount >= 0;
    }

    @JsonIgnore
    public boolean isNegative() {
        return !this.isPositive();
    }

    public Amount calculate(Quantity quantity) {
        return new Amount(this.getAmount() * quantity.getNumberOfItems());
    }

    public Amount adjustSign() {
        return new Amount(this.getAmount() * (-1));
    }

    public Amount add(Amount value) {
        return new Amount(this.amount + value.getAmount());
    }

    public Amount multiply(Amount value) {
        return new Amount(this.amount * value.getAmount());
    }

    @Override
    public String format(Settings settings) {
        Locale locale = settings.getValueOrDefault(SettingPair.Property.LANGUAGE);
        Currency currency = settings.getValueOrDefault(SettingPair.Property.CURRENCY);
        StringBuilder result = new StringBuilder(String.format(locale, "%.2f", amount)).append(" ");
        if (settings.getValueOrDefault(SettingPair.Property.SHOW_CURRENCY_SIGN).equals(Boolean.toString(true))) {
            result.append(currency.getSymbol());
        } else {
            result.append(currency.getCurrencyCode());
        }
        return result.toString();
    }

    /*
     * Getters and Setters
     */

    public double getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount1 = (Amount) o;
        return Double.compare(amount1.amount, amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return "Amount [" +
                "amount=" + amount +
                ']';
    }

}
